<template>
    <div id="pane">
        <canvas id="canvas"
                ref="canvas"
                @mousedown="onDrawStart"
                @mousemove="onDrawing"/>
        <draggable v-for="node in nodes"
                   :key="node.key"
                   :x="node.x"
                   :y="node.y"
                   :payload="node"
                   @dragging="onDragging"
                   @drag-end="onDragEnd">
            <template v-slot="{pos}">
                <div :ref="'node'+node.key">{{ node.content }}({{ pos.x }},{{ pos.y }})</div>
            </template>
        </draggable>
    </div>
</template>

<script>
    import Draggable from "@/components/Draggable";
    
    export default {
        name: "Page1",
        components: {Draggable},
        async created() {
            this.buildNodes(this.tree);
            window.addEventListener("mouseup", this.onDrawEnd)
            window.addEventListener("resize", this.initCanvas)
            
            await this.$nextTick();
            this.initCanvas();
        },
        data() {
            return {
                tree: {
                    key: 1,
                    content: "Node1",
                    children: [
                        {key: 2, content: "Node2"},
                        {
                            key: 3, content: "Node3",
                            children: [
                                {key: 5, content: "Node5"},
                                {key: 6, content: "Node6"}
                            ]
                        },
                        {key: 4, x: 0, y: 0, content: "Node4"},
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
            initCanvas() {
                this.$refs.canvas.width = this.$refs.canvas.offsetWidth;
                this.$refs.canvas.height = this.$refs.canvas.offsetHeight;
                this.calcBounds();
                this.alignTree();
                this.drawLines();
            },
            calcBounds(node = this.tree) {
                //坑，v-for中的ref是个数组
                const element = this.$refs["node" + node.key][0];
                node.selfWidth = element.offsetWidth + 60;
                node.selfHeight = element.offsetHeight + 40;
                node.treeWidth = node.selfWidth;
                node.treeHeight = node.selfHeight;
                
                if (!node.children || !node.children.length) {
                    return
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
                node.treeWidth += maxChildTreeWidth;
                node.treeHeight = Math.max(node.selfHeight, totalChildTreeHeight)
            },
            alignTree(node = this.tree, lastY = 100) {
                if (node.parent) {
                    node.x = node.parent.x + node.parent.selfWidth;
                } else {
                    node.x = 100;
                }
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
                } else {
                    node.y = lastY;
                }
            },
            drawLines() {
                let context = this.$refs.canvas.getContext("2d");
                let canvasWidth = this.$refs.canvas.offsetWidth;
                let canvasHeight = this.$refs.canvas.offsetHeight;
                context.clearRect(0, 0, canvasWidth, canvasHeight)
                
                const drawLine = (x1, y1, x2, y2) => {
                    let cpx1 = x1 + (x2 - x1) / 2;
                    let cpx2 = x2 - (x2 - x1) / 2;
                    context.strokeStyle = "blue"
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
                        drawLine(x1, y1, x2, y2);
                        lineToChildren(child);
                    }
                };
                
                lineToChildren(this.tree);
            },
            onDragging(event) {
                // console.log(`onDragging:${event.payload.key},${event.x},${event.y}`)
                event.payload.x = event.x;
                event.payload.y = event.y;
                this.drawLines();
            },
            onDragEnd(event) {
                console.log(`onDragEnd:${event.payload.key},${event.x},${event.y}`)
                this.alignTree();
                this.drawLines();
            },
            onDrawStart(event) {
                if (event.button !== 0) {
                    return
                }
                
                // console.log("event.offsetX:" + event.offsetX)
                // console.log("event.offsetY:" + event.offsetY)
                
                this.drawing = true;
                
                let context = this.$refs.canvas.getContext("2d");
                context.strokeStyle = "blue"
                context.beginPath();
                context.moveTo(event.offsetX, event.offsetY);
            },
            onDrawing(event) {
                if (!this.drawing) {
                    return;
                }
                
                // console.log("event.offsetX:" + event.offsetX)
                
                // let canvasWidth = this.$refs.canvas.offsetWidth;
                // let canvasHeight = this.$refs.canvas.offsetHeight;
                let context = this.$refs.canvas.getContext("2d");
                
                // context.clearRect(0, 0, canvasWidth, canvasHeight)
                context.lineTo(event.offsetX, event.offsetY);
                context.stroke();
            },
            onDrawEnd(event) {
                console.log("event.offsetY:" + event.offsetY)
                console.log("event.clientY:" + event.clientY)
                this.drawing = false;
            }
        }
    }
</script>

<style scoped>
    #pane {
        position: absolute;
        width: 100%;
        height: 100%;
        overflow: hidden;
    }
    
    #canvas {
        position: absolute;
        width: 100%;
        height: 100%;
        background-color: aliceblue;
    }
</style>