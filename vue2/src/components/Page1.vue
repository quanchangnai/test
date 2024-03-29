<template>
    <div id="container">
        <div id="left">left</div>
        <div id="center">
            <draggable id="board"
                       @drag-end="onBoardDragEnd"
                       @contextmenu.native="onBoardContextMenu"
                       @mouseup.native="onBoardMouseUp">
                <canvas id="canvas" @contextmenu.prevent/>
                <draggable v-for="node in nodes"
                           :key="node.key"
                           :x="node.x"
                           :y="node.y"
                           :payload="node"
                           @drag-start="onNodeDragStart"
                           @dragging="onNodeDragging"
                           @drag-end="onNodeDragEnd"
                           @contextmenu.native="onNodeContextMenu">
                    <template v-slot="{pos}">
                        <div class="node" :ref="'node'+node.key">tid:{{ node.tid }},key:{{ node.key }}@({{ pos.x }},{{ pos.y }})</div>
                    </template>
                </draggable>
            </draggable>
        </div>
        <div id="right">
            <el-table :data="templates" size="medium" stripe border>
                <el-table-column>
                    <template #header>
                        <el-input clearable size="medium" placeholder="输入关键字搜索" prefix-icon="el-icon-search"/>
                    </template>
                    <template #default="{row}">
                        <div style="cursor: pointer;user-select: none" @mousedown="e=>onTemplateSelect(e,row.id)"> {{ row.name }}</div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <draggable id="tempNode"
                   v-if="tempNode!=null"
                   :x="tempNode.x"
                   :y="tempNode.y"
                   :payload="tempNode"
                   :initDragging="true"
                   @drag-start="onNodeDragStart"
                   @dragging="onNodeDragging"
                   @drag-end="onNodeDragEnd">
            <div class="node" :ref="'node'+tempNode.key">tid:{{ tempNode.tid }},key:{{ tempNode.key }}</div>
        </draggable>
    </div>
</template>

<script>
import Draggable from "./Draggable";
import utils from "../utils";

const nodeSpaceX = 60;//节点x轴间隔空间
const nodeSpaceY = 40;//节点y轴间隔空间
const boardEdgeSpace = 100;//画板边缘空间

export default {
    name: "Page1",
    components: {Draggable},
    created() {
        this.buildNodes(this.tree);
        window.addEventListener("resize", this.drawCanvas);
    },
    destroyed() {
        window.removeEventListener("resize", this.drawCanvas);
    },
    mounted() {
        this.drawCanvas();
        this.$nextTick(this.drawCanvas);
    },
    data() {
        return {
            templates: [{id: 1, name: "节点模板1"}, {id: 2, name: "节点模板2"}],
            tree: {
                key: 1,
                tid: 1,
                children: [
                    {key: 2, tid: 1},
                    {
                        key: 3, tid: 1,
                        children: [
                            {
                                key: 5, tid: 1,
                                children: [
                                    {
                                        key: 6, tid: 1,
                                        children: [
                                            {
                                                key: 7, tid: 1,
                                                children: [
                                                    {
                                                        key: 8, tid: 1,
                                                        children: [
                                                            {key: 9, tid: 1},
                                                        ]
                                                    },
                                                ]
                                            },
                                        ]
                                    },
                                ]
                            },
                        ]
                    },
                    {key: 4, tid: 1},
                ]
            },
            nodes: [],
            maxNodeKey: 100,
            tempNode: null,
            boardX: 0,
            boardY: 0,
        }
    },
    methods: {
        buildNodes(node) {
            if (node == null) {
                return;
            }
            this.maxNodeKey = Math.max(this.maxNodeKey, node.key);
            this.$set(node, "x", 0);
            this.$set(node, "y", 0);
            this.nodes.push(node);
            if (node.children) {
                for (let child of node.children) {
                    child.parent = node;
                    this.buildNodes(child);
                }
            }
        },
        drawCanvas() {
            this.calcBounds();

            if (this.tree) {
                const board = document.querySelector("#board");
                board.style.width = Math.max(board.parentElement.offsetWidth, this.tree.treeWidth + boardEdgeSpace * 2) + "px";
                board.style.height = Math.max(board.parentElement.offsetHeight, this.tree.treeHeight + boardEdgeSpace * 2) + "px";
            }

            this.alignTree();
            this.drawLines();
        },
        calcBounds(node = this.tree) {
            if (!node) {
                return;
            }
            //坑，v-for中的ref是个数组
            let element;
            if (node === this.tempNode) {
                element = this.$refs["node" + node.key];
            } else {
                element = this.$refs["node" + node.key][0];
            }
            node.selfWidth = element.offsetWidth + nodeSpaceX;
            node.selfHeight = element.offsetHeight + nodeSpaceY;

            if (!node.children || !node.children.length) {
                node.treeWidth = node.selfWidth;
                node.treeHeight = node.selfHeight;
                return;
            }

            let maxChildTreeWidth = 0;
            let totalChildTreeHeight = 0;

            for (let child of node.children) {
                this.calcBounds(child);
                if (child.treeWidth > maxChildTreeWidth) {
                    maxChildTreeWidth = child.treeWidth;
                }
                totalChildTreeHeight += child.treeHeight;
            }

            node.treeWidth = node.selfWidth + maxChildTreeWidth;
            node.treeHeight = Math.max(node.selfHeight, totalChildTreeHeight)
        },
        alignTree(node = this.tree, lastY = boardEdgeSpace) {
            if (!node) {
                return;
            }
            if (node.parent) {
                node.x = node.parent.x + node.parent.selfWidth;
            } else {
                node.x = boardEdgeSpace;
            }

            node.y = lastY;

            if (node.children && node.children.length) {
                for (let child of node.children) {
                    this.alignTree(child, lastY);
                    lastY += child.treeHeight;
                }
                if (node.children.length > 1) {
                    node.y = (node.children[0].y + node.children[node.children.length - 1].y) / 2;
                } else {
                    node.y = node.children[0].y;
                }
            }

        },
        drawLines() {
            const canvas = document.querySelector("#canvas");
            canvas.width = canvas.offsetWidth;
            canvas.height = canvas.offsetHeight;
            const context = canvas.getContext("2d");
            context.clearRect(0, 0, canvas.width, canvas.height);

            const drawLine = (x1, y1, x2, y2) => {
                let cpx1 = x1 + (x2 - x1) / 2;
                let cpx2 = x2 - (x2 - x1) / 2;
                context.beginPath();
                context.moveTo(x1, y1);
                context.bezierCurveTo(cpx1, y1, cpx2, y2, x2, y2);
                context.stroke();
            };

            const lineToChildren = node => {
                if (!node || !node.children) {
                    return;
                }

                let x1 = node.x + node.selfWidth - nodeSpaceX;
                let y1 = node.y + (node.selfHeight - nodeSpaceY) / 2;

                for (let child of node.children) {
                    let x2 = child.x;
                    let y2 = child.y + (child.selfHeight - nodeSpaceY) / 2;
                    if (child.dragging) {
                        context.strokeStyle = "red"
                    } else {
                        context.strokeStyle = "blue"
                    }
                    drawLine(x1, y1, x2, y2);
                    lineToChildren(child);
                }
            };

            lineToChildren(this.tree);

            if (this.tempNode && this.tempNode.parent) {
                context.strokeStyle = "red";
                let x1 = this.tempNode.parent.x + this.tempNode.parent.selfWidth - nodeSpaceX;
                let y1 = this.tempNode.parent.y + (this.tempNode.parent.selfHeight - nodeSpaceY) / 2;
                let x2 = this.tempNode.x - document.querySelector("#left").offsetWidth - this.boardX;
                let y2 = this.tempNode.y + (this.tempNode.selfHeight - nodeSpaceY) / 2 - this.boardY;
                drawLine(x1, y1, x2, y2);
            }
        },
        onNodeDragStart(event) {
            event.payload.dragging = true;
        },
        onNodeDragging(event) {
            // console.log(`onDragging:${event.payload.key},${event.x},${event.y}`)
            let node = event.payload;
            const moveX = event.x - node.x;
            const moveY = event.y - node.y;

            const moveTree = node0 => {
                node0.x += moveX;
                node0.y += moveY;
                if (node0.children) {
                    for (let child of node0.children) {
                        moveTree(child);
                    }
                }
            };
            moveTree(node);

            this.linkParentNode(node);

            this.drawLines();
        },
        findParentNode(node) {
            let deltaX = 0;
            let deltaY = 0;
            if (node === this.tempNode) {
                deltaX = document.querySelector("#left").offsetWidth + this.boardX;
                deltaY = this.boardY;
            }

            //寻找最近的的节点作为父节点
            let nearestNode = null;
            let minDistance = -1;

            const find = node0 => {
                if (!node0 || node0 === node) {
                    return;
                }
                let x0 = deltaX + node0.x + node0.selfWidth - nodeSpaceX;
                let y0 = deltaY + node0.y + (node0.selfHeight - nodeSpaceY) / 2;
                let distance = (node.x - x0) ** 2 + (node.y - y0) ** 2;
                if (minDistance < 0 || distance < minDistance) {
                    minDistance = distance;
                    nearestNode = node0;
                }
                if (node0.children) {
                    for (let child of node0.children) {
                        find(child);
                    }
                }
            };

            find(this.tree);

            return nearestNode;
        },
        linkParentNode(node, parentNode) {
            if (parentNode == null) {
                parentNode = this.findParentNode(node);
            }
            if (parentNode == null) {
                return;
            }

            if (node === this.tempNode) {
                node.parent = parentNode;
                return;
            }

            //关联父子节点
            if (node.parent && node.parent.children) {
                let nodeIndex = node.parent.children.indexOf(node);
                if (nodeIndex >= 0) {
                    node.parent.children.splice(nodeIndex, 1);
                }
            }
            node.parent = parentNode;
            if (!parentNode.children) {
                parentNode.children = [];
            }
            parentNode.children.push(node);

            //按y轴排序兄弟节点
            parentNode.children.sort((n1, n2) => n1.y - n2.y);
        },
        onNodeDragEnd(event) {
            console.log(`onDragEnd:${event.payload.key},${event.x},${event.y}`);
            event.payload.dragging = false;
            this.drawCanvas()
        },
        onNodeContextMenu(event) {
            console.log("onNodeContextMenu:" + event.currentTarget.id)
        },
        onBoardDragEnd(event) {
            console.log("onBoardDragEnd:" + JSON.stringify(event));
            this.boardX = event.x;
            this.boardY = event.y;
        },
        onBoardContextMenu(event) {
            console.log("onBoardContextMenu:" + event.currentTarget.id)
        },
        onBoardMouseUp() {
            console.log("onBoardMouseUp");
            if (this.tempNode == null) {
                return;
            }
            let tempNode = this.tempNode;
            this.tempNode = null;
            this.nodes.push(tempNode);
            tempNode.dragging = false;
            if (tempNode.parent) {
                this.linkParentNode(tempNode, tempNode.parent);
            } else {
                this.tree = tempNode;
            }

            this.$nextTick(() => {
                this.drawCanvas();
            });
        },
        onTemplateSelect(event, tid) {
            console.log("onTemplateSelect:" + event.clientX + "," + event.clientY);
            let container = document.querySelector("#container");
            let containerX = utils.getClientX(container);
            let containerY = utils.getClientY(container);
            this.tempNode = {key: this.maxNodeKey++, tid: tid, x: event.clientX - containerX, y: event.clientY - containerY};

            this.$nextTick(() => {
                let tempNode = document.querySelector("#tempNode");
                this.tempNode.x -= tempNode.offsetWidth / 2;
                this.tempNode.y -= tempNode.offsetHeight / 2;
                this.calcBounds(this.tempNode);
                this.linkParentNode(this.tempNode);
                this.drawLines();
            });

            window.addEventListener("mouseup", () => {
                this.tempNode = null;
                this.drawLines();
            }, {once: true});
        }
    }
}
</script>

<style scoped>
#left {
    position: absolute;
    width: 250px;
    height: 100%;
    border: solid 1px red;
}

#right {
    position: absolute;
    width: 250px;
    height: 100%;
    right: 0;
    border: solid 1px blue;
    user-select: none;
}

#center {
    position: absolute;
    left: 255px;
    right: 255px;
    height: 100%;
    overflow: hidden;
    border: solid 1px green;
}

#board {
    width: 100%;
    height: 100%;
    overflow: hidden;
}

#canvas {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: aliceblue;
    /* background-image: linear-gradient(0deg, transparent 49%, #e8e9ec 50%, transparent 51%), linear-gradient(90deg, transparent 49%, #e8e9ec 50%, transparent 51%);
     background-size: 15px 15px;*/
}

.node {
    min-width: 150px;
    height: 50px;
    background-color: #99ccff;
    text-align: center;
    line-height: 50px;
    border: 1px solid #98a5e9;
    border-radius: 5px;
}

.node:hover {
    cursor: pointer;
}

#tempNode {
    /*鼠标事件穿透该div*/
    pointer-events: none;
    z-index: 1;
}
</style>