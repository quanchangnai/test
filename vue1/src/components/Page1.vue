<template>
    <div id="panel" ref="panel">
        <canvas id="canvas"
                ref="canvas"
                @mousedown="onDrawStart"
                @mousemove="onDrawing"
                @mouseup="onDrawEnd"/>
        <draggable :x="x"
                   :y="y"
                   @dragging="onDragging"
                   @drag-end="onDragEnd"/>
    </div>
</template>

<script>
    import Draggable from "@/components/Draggable";
    
    export default {
        name: "Page1",
        components: {Draggable},
        async created() {
            await this.$nextTick();
            this.$refs.canvas.width = this.$refs.canvas.offsetWidth
            this.$refs.canvas.height = this.$refs.canvas.offsetHeight
        },
        data() {
            return {
                drawing: false,
                x: 100,
                y: 100
            }
        },
        methods: {
            onDragging() {
                // console.log("event:" + JSON.stringify(event))
            },
            onDragEnd(event) {
                console.log("event:" + JSON.stringify(event))
                
                this.x = event.x
                this.y = event.y
                
                this.$nextTick(() => {
                    if (this.x < 0 || this.x > this.$refs.panel.offsetWidth - event.width) {
                        this.x = 100
                    }
                    if (this.y < 0 || this.y > this.$refs.panel.offsetHeight - event.height) {
                        this.y = 100
                    }
                })
            },
            onDrawStart(event) {
                if (event.button !== 0) {
                    return
                }
                
                console.log("event.offsetX:" + event.offsetX)
                console.log("event.offsetY:" + event.offsetY)
                
                this.drawing = true;
                
                let context = this.$refs.canvas.getContext("2d");
                context.strokeStyle = "red"
                context.beginPath();
                context.moveTo(event.offsetX, event.offsetY);
            },
            onDrawing(event) {
                if (!this.drawing) {
                    return;
                }
                
                console.log("event.offsetX:" + event.offsetX)
                
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
            },
        }
    }
</script>

<style scoped>
    #panel {
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