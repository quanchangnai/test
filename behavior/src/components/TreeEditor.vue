<template>
    <div id="container">
        <div id="left">
            <el-table :data="trees"
                      ref="treesTable"
                      size="medium"
                      highlight-current-row
                      @current-change="onTreeSelect"
                      stripe border>
                <el-table-column>
                    <template #header>
                        <el-input clearable size="medium" placeholder="输入关键字搜索" prefix-icon="el-icon-search"/>
                    </template>
                    <template #default="{row}">
                        <div> {{ row.id }}:{{ row.name }}</div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div id="center">
            <draggable id="board"
                       :x="boardX"
                       :y="boardY"
                       @drag-end="onBoardDragEnd"
                       @contextmenu.native="onBoardContextMenu"
                       @mouseup.native="onBoardMouseUp">
                <canvas id="canvas" @contextmenu.prevent/>
                <draggable v-for="node in nodes"
                           :key="node.id"
                           :x="node.x"
                           :y="node.y"
                           :payload="node"
                           @drag-start="onNodeDragStart"
                           @dragging="onNodeDragging"
                           @drag-end="onNodeDragEnd"
                           @contextmenu.native="onNodeContextMenu">
                    <template>
                        <div class="node" :ref="'node'+node.id">tid:{{ node.tid }},id:{{ node.id }}</div>
                        <div v-if="node.children&&node.children.length"
                             @click="()=>onNodeFold(node)"
                             :class="node.folded?'el-icon-circle-plus-outline':'el-icon-remove-outline'"
                             class="node-fold-icon"/>
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
            <div class="node" :ref="'node'+tempNode.id">tid:{{ tempNode.tid }},id:{{ tempNode.id }}</div>
        </draggable>
    </div>
</template>

<script>
import Draggable from "./Draggable";
import utils from "../utils";
import {ipcRenderer} from 'electron'

const nodeSpaceX = 60;//节点x轴间隔空间
const nodeSpaceY = 40;//节点y轴间隔空间
const boardEdgeSpace = 100;//画板边缘空间

export default {
    name: "TreeEditor",
    components: {Draggable},
    async created() {
        this.templates = await ipcRenderer.invoke("load-templates");
        this.trees = await ipcRenderer.invoke("load-trees");
        this.tree = this.trees[0];
        await this.$nextTick();//等待界面渲染
        this.drawCanvas();
        window.addEventListener("resize", this.drawCanvas);
    },
    destroyed() {
        window.removeEventListener("resize", this.drawCanvas);
    },
    data() {
        return {
            templates: null,
            trees: null,
            tree: null,
            maxNodeId: 0,
            tempNode: null,
            boardX: 0,
            boardY: 0,
        }
    },
    computed: {
        nodes() {
            let result = [];
            let build = node => {
                if (node == null) {
                    return;
                }
                if (node.x === undefined) {
                    this.$set(node, "x", 0);
                }
                if (node.y === undefined) {
                    this.$set(node, "y", 0);
                }
                result.push(node);
                this.maxNodeId = Math.max(this.maxNodeId, node.id);

                if (node.children && !node.folded) {
                    for (let child of node.children) {
                        child.parent = node;
                        build(child);
                    }
                }
            };

            build(this.tree);
            return result;
        }
    },
    methods: {
        async onTreeSelect(tree) {
            this.tree = tree;
            this.maxNodeId = 0;
            await this.$nextTick();//等待界面删除旧节点
            this.drawCanvas();
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
                element = this.$refs["node" + node.id];
            } else {
                element = this.$refs["node" + node.id][0];
            }
            //界面渲染完成之后才能取到元素大小
            node.selfWidth = element.offsetWidth + nodeSpaceX;
            node.selfHeight = element.offsetHeight + nodeSpaceY;

            if (!node.children || !node.children.length || node.folded) {
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

            if (node.children && node.children.length && !node.folded) {
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
                if (!node || !node.children || node.folded) {
                    return;
                }

                let x1 = node.x + node.selfWidth - nodeSpaceX + 14;
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
            event.payload.dragging = false;
            this.drawCanvas();
            this.saveTree();
        },
        saveTree() {
            let buildTree = tree => {
                let resultTree = {id: tree.id, name: tree.name, tid: tree.tid};
                if (tree.children) {
                    resultTree.children = [];
                    for (let child of tree.children) {
                        resultTree.children.push(buildTree(child))
                    }
                }
                return resultTree;
            };

            ipcRenderer.invoke("save-tree", buildTree(this.tree));
        },
        onNodeContextMenu(event) {
            console.log("onNodeContextMenu:" + event.currentTarget.id)
        },
        onNodeFold(node) {
            node.folded = !node.folded;
            this.$nextTick(this.drawCanvas);
        },
        async onBoardDragEnd(event) {
            this.boardX = event.x;
            this.boardY = event.y;

            await this.$nextTick();//等待this.boardX修改生效

            //如果拖出界了就拉回到初始位置
            let board = document.querySelector("#board");
            let center = document.querySelector("#center");
            if (event.x < -board.offsetWidth + boardEdgeSpace || event.x > center.offsetWidth - boardEdgeSpace) {
                this.boardX = 0;
                this.boardY = 0;
            }
            if (event.y < -board.offsetHeight + boardEdgeSpace || event.y > center.offsetHeight - boardEdgeSpace) {
                this.boardX = 0;
                this.boardY = 0;
            }
        },
        onBoardContextMenu(event) {
            console.log("onBoardContextMenu:" + event.currentTarget.id)
        },
        onBoardMouseUp() {
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
                this.saveTree();
            });
        },
        onTemplateSelect(event, tid) {
            let container = document.querySelector("#container");
            let containerX = utils.getClientX(container);
            let containerY = utils.getClientY(container);
            this.tempNode = {id: ++this.maxNodeId, tid: tid, x: event.clientX - containerX, y: event.clientY - containerY};

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
}

#right {
    position: absolute;
    width: 250px;
    height: 100%;
    right: 0;
    user-select: none;
}

/*noinspection CssUnusedSymbol*/
.el-table {
    height: 100%;
}

#center {
    position: absolute;
    left: 250px;
    right: 250px;
    height: 100%;
    overflow: hidden;
    border-top: solid 1px #dcdfe6;
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
    min-width: 120px;
    height: 40px;
    background-color: #99ccff;
    text-align: center;
    line-height: 40px;
    border: 1px solid #98a5e9;
    border-radius: 5px;
}

.node:hover {
    cursor: pointer;
    background-color: #00981a;
}

.node-fold-icon {
    position: absolute;
    top: calc(50% - 8px);
    left: calc(100% - 1px);
    cursor: default
}

#tempNode {
    /*鼠标事件穿透该div*/
    pointer-events: none;
    z-index: 1;
}
</style>