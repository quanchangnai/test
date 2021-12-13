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
                <tree-node v-for="node in nodes"
                           :key="node.id"
                           :ref="'node'+node.id"
                           :node="node"
                           @dragging="onNodeDragging"
                           @drag-end="onNodeDragEnd"
                           @detail="drawCanvas"
                           @collapse="drawCanvas"
                           @delete="onNodeDelete"/>
            </draggable>
        </div>
        <div id="right">
            <el-table :data="templates" size="medium" stripe border>
                <el-table-column>
                    <template #header>
                        <el-input clearable size="medium" placeholder="输入关键字搜索" prefix-icon="el-icon-search"/>
                    </template>
                    <template #default="{row}">
                        <div style="cursor: grab;user-select: none;" @mousedown="e=>onTemplateSelect(e,row.id)"> {{ row.name }}</div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <tree-node v-if="tempNode!=null"
                   :ref="'node'+tempNode.id"
                   :node="tempNode"
                   :temp="true"
                   @dragging="onNodeDragging"
                   @drag-end="onNodeDragEnd"/>
    </div>
</template>

<script>
import Draggable from "./Draggable";
import TreeNode from "./TreeNode";
import utils from "../utils";
import {ipcRenderer} from 'electron'

const nodeSpaceX = 50;//节点x轴间隔空间
const nodeSpaceY = 30;//节点y轴间隔空间
const boardEdgeSpace = 100;//画板边缘空间

export default {
    name: "TreeEditor",
    components: {TreeNode, Draggable},
    async created() {
        this.templates = await ipcRenderer.invoke("load-templates");
        this.trees = await ipcRenderer.invoke("load-trees");
        this.tree = this.trees[0];
        this.$refs.treesTable.setCurrentRow(this.tree);
        await this.drawCanvas();
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

                if (node.children && !node.collapsed) {
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
        onTreeSelect(tree) {
            this.tree = tree;
            this.maxNodeId = 0;
            this.drawCanvas();
        },
        async drawCanvas() {
            //等待界面刷新后才能获得元素大小
            await this.$nextTick();

            const draw = () => {
                this.calcBounds();

                if (this.tree) {
                    const board = document.querySelector("#board");
                    board.style.width = Math.max(board.parentElement.offsetWidth, this.tree.treeWidth + boardEdgeSpace * 2) + "px";
                    board.style.height = Math.max(board.parentElement.offsetHeight, this.tree.treeHeight + boardEdgeSpace * 2) + "px";
                }

                this.alignTree();
                this.drawLines();
            };

            draw();

            //节点有时候会先被撑大再还原导致calcBounds不准确，延时再执行一次
            await this.$nextTick();
            draw();
        },
        calcBounds(node = this.tree) {
            if (!node) {
                return;
            }

            //坑，v-for中的ref是个数组
            let nodeContent;
            if (node === this.tempNode) {
                nodeContent = this.$refs["node" + node.id].content();
            } else {
                nodeContent = this.$refs["node" + node.id][0].content();
            }

            //界面渲染完成之后才能取到元素大小
            node.selfWidth = nodeContent.offsetWidth + nodeSpaceX;
            node.selfHeight = nodeContent.offsetHeight + nodeSpaceY;

            if (!node.children || !node.children.length || node.collapsed) {
                node.treeWidth = node.selfWidth;
                node.treeHeight = node.selfHeight;
                return;
            }

            let maxChildWidth = 0;
            let childrenHeight = 0;

            for (let child of node.children) {
                this.calcBounds(child);
                if (child.treeWidth > maxChildWidth) {
                    maxChildWidth = child.treeWidth;
                }
                childrenHeight += child.treeHeight;
            }

            node.treeWidth = node.selfWidth + maxChildWidth;
            node.treeHeight = Math.max(node.selfHeight, childrenHeight);
            node.childrenHeight = childrenHeight;
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

            if (!node.children || !node.children.length || node.collapsed) {
                node.y = lastY;
                return;
            }

            if (node.treeHeight <= node.childrenHeight) {
                for (let child of node.children) {
                    this.alignTree(child, lastY);
                    lastY += child.treeHeight;
                }
                if (node.children.length > 1) {
                    let lastChild = node.children[node.children.length - 1];
                    node.y = (node.children[0].y + lastChild.y + lastChild.selfHeight - node.selfHeight) / 2;
                } else {
                    node.y = node.children[0].y + node.children[0].selfHeight / 2 - node.selfHeight / 2;
                }
            } else {
                // 父节点比所有子树高度之和还高
                node.y = lastY;
                lastY += (node.selfHeight - node.childrenHeight) / 2;
                for (let child of node.children) {
                    this.alignTree(child, lastY);
                    lastY += child.treeHeight;
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

            const nodeCollapseIconWidth = 14;//节点收起子树图标宽度

            const lineToChildren = node => {
                if (!node || !node.children || node.collapsed) {
                    return;
                }

                let x1 = node.x + node.selfWidth - nodeSpaceX + nodeCollapseIconWidth;
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
                let tempParentNode = this.tempNode.parent;
                let x1 = tempParentNode.x + tempParentNode.selfWidth - nodeSpaceX;
                if (tempParentNode.children && tempParentNode.children.length) {
                    x1 += nodeCollapseIconWidth;
                }
                let y1 = tempParentNode.y + (tempParentNode.selfHeight - nodeSpaceY) / 2;
                let x2 = this.tempNode.x - document.querySelector("#left").offsetWidth - this.boardX;
                let y2 = this.tempNode.y + (this.tempNode.selfHeight - nodeSpaceY) / 2 - this.boardY;
                drawLine(x1, y1, x2, y2);
            }
        },
        onNodeDragging(node) {
            this.linkParentNode(node);
            this.drawLines();
        },
        async onNodeDragEnd() {
            await this.drawCanvas();
            this.saveTree();
        },
        onNodeDelete(node) {
            if (node === this.tree) {
                this.tree = null;
            } else {
                node.parent.children.splice(node.parent.children.indexOf(node), 1);
            }
            this.drawCanvas();
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
                this.$set(parentNode, "children", []);
            }
            parentNode.children.push(node);

            //按y轴排序兄弟节点
            parentNode.children.sort((n1, n2) => n1.y - n2.y);
        },
        findParentNode(node) {
            let deltaX = 0;
            let deltaY = 0;
            if (node === this.tempNode) {
                deltaX = document.querySelector("#left").offsetWidth + this.boardX;
                deltaY = this.boardY;
            }

            let x = node.x;
            let y = node.y + (node.selfHeight - nodeSpaceY) / 2;

            //寻找最近的的节点作为父节点
            let nearestNode = null;
            let minDistance = -1;

            const find = node0 => {
                if (!node0 || node0 === node || node0.collapsed) {
                    return;
                }
                let x0 = deltaX + node0.x + node0.selfWidth - nodeSpaceX;
                let y0 = deltaY + node0.y + (node0.selfHeight - nodeSpaceY) / 2;

                let distance = (x - x0) ** 2 + (y - y0) ** 2;
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
        saveTree() {
            let build = node => {
                let result = {id: node.id, name: node.name, tid: node.tid};
                if (node.children) {
                    result.children = [];
                    for (let child of node.children) {
                        result.children.push(build(child))
                    }
                }
                return result;
            };

            ipcRenderer.invoke("save-tree", build(this.tree));
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
        async onBoardMouseUp() {
            if (this.tempNode == null) {
                return;
            }
            let tempNode = this.tempNode;
            this.tempNode = null;
            tempNode.dragging = false;
            if (tempNode.parent) {
                this.linkParentNode(tempNode, tempNode.parent);
            } else {
                this.tree = tempNode;
            }

            await this.drawCanvas();
            this.saveTree();
        },
        onTemplateSelect(event, tid) {
            let container = document.querySelector("#container");
            let x = event.clientX - utils.getClientX(container);
            let y = event.clientY - utils.getClientY(container);
            this.tempNode = {id: ++this.maxNodeId, tid: tid, x, y, collapsed: false};

            this.$nextTick(() => {
                let tempNodeContent = this.$refs["node" + this.tempNode.id].content();
                this.tempNode.x -= tempNodeContent.offsetWidth / 2;
                this.tempNode.y -= tempNodeContent.offsetHeight / 2;
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
    box-sizing: border-box;
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

</style>