<template>
    <div>
        <div id="left">left</div>
        <div id="center">
            <draggable id="board">
                <canvas id="canvas"/>
                <draggable v-for="node in nodes"
                           :key="node.key"
                           :x="node.x"
                           :y="node.y"
                           :payload="node"
                           @drag-start="onNodeDragStart"
                           @dragging="onNodeDragging"
                           @drag-end="onNodeDragEnd"
                           v-slot="{pos}">
                    <div class="node" :ref="'node'+node.key">{{ node.content }}({{ pos.x }},{{ pos.y }})</div>
                </draggable>
            </draggable>
        </div>
        <div id="right">right</div>
    </div>
</template>

<script>
import Draggable from "@/components/Draggable";

export default {
    name: "Page1",
    components: {Draggable},
    created() {
        this.buildNodes(this.tree);
        window.addEventListener("resize", this.drawCanvas)
    },
    destroyed() {
        window.removeEventListener("resize", this.drawCanvas)
    },
    mounted() {
        this.drawCanvas();
    },
    data() {
        return {
            tree: {
                key: 1, content: "Node1",
                children: [
                    {key: 2, content: "Node2"},
                    {
                        key: 3, content: "Node3",
                        children: [
                            {
                                key: 5, content: "Node5",
                                children: [
                                    {
                                        key: 6, content: "Node6",
                                        children: [
                                            {
                                                key: 7, content: "Node7",
                                                children: [
                                                    {
                                                        key: 8, content: "Node8",
                                                        children: [
                                                            {key: 9, content: "Node9"},
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
                    {key: 4, content: "Node4"},
                ]
            },
            nodes: [],
            drawing: false,
        }
    },
    methods: {
        buildNodes(node) {
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

            const board = document.querySelector("#board");
            if (board.offsetWidth <= this.tree.treeWidth + 200) {
                board.style.width = (this.tree.treeWidth + 200) + "px"
            } else {
                board.style.width = "100%"
            }
            if (board.offsetHeight <= this.tree.treeHeight + 200) {
                board.style.height = (this.tree.treeHeight + 200) + "px"
            } else {
                board.style.height = "100%"
            }

            this.alignTree();
            this.drawLines();
        },
        calcBounds(node = this.tree) {
            //坑，v-for中的ref是个数组
            const element = this.$refs["node" + node.key][0];
            node.selfWidth = element.offsetWidth + 60;
            node.selfHeight = element.offsetHeight + 40;

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
        alignTree(node = this.tree, lastY = 100) {
            if (node.parent) {
                node.x = node.parent.x + node.parent.selfWidth;
            } else {
                node.x = 100;
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
            context.clearRect(0, 0, canvas.offsetWidth, canvas.offsetHeight);

            const drawLine = (x1, y1, x2, y2) => {
                let cpx1 = x1 + (x2 - x1) / 2;
                let cpx2 = x2 - (x2 - x1) / 2;
                // context.strokeStyle = "blue"
                context.beginPath();
                context.moveTo(x1, y1);
                context.bezierCurveTo(cpx1, y1, cpx2, y2, x2, y2);
                context.stroke();
            };

            const lineToChildren = node => {
                if (!node.children) {
                    return;
                }

                let x1 = node.x + node.selfWidth - 60;
                let y1 = node.y + (node.selfHeight - 40) / 2;

                for (let child of node.children) {
                    let x2 = child.x;
                    let y2 = child.y + (child.selfHeight - 40) / 2;
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
        },
        onNodeDragStart(event) {
            event.payload.dragging = true;
        },
        onNodeDragging(event) {
            // console.log(`onDragging:${event.payload.key},${event.x},${event.y}`)
            const moveX = event.x - event.payload.x;
            const moveY = event.y - event.payload.y;

            const moveTree = node => {
                node.x += moveX;
                node.y += moveY;
                if (node.children) {
                    for (let child of node.children) {
                        moveTree(child);
                    }
                }
            };
            moveTree(event.payload);
            this.drawLines();
        },
        onNodeDragEnd(event) {
            // console.log(`onDragEnd:${event.payload.key},${event.x},${event.y}`);
            // console.log(`onDragEnd:${event.payload.key},${event.width},${event.height}`)
            event.payload.dragging = false;
            this.drawCanvas()
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
    overflow: hidden
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
</style>