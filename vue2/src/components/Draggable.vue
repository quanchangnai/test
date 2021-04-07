<template>
    <div ref="draggable"
         class="draggable"
         :style="{left: left + 'px', top: top + 'px'}"
         @mousedown.stop="onMouseDown">
        <slot :pos="{x:left,y:top}">
            <div>测试:({{ left }},{{ top }})</div>
        </slot>
    </div>
</template>

<script>
    export default {
        name: "Draggable",
        props: {
            x: {
                type: Number,
                default: 0
            },
            y: {
                type: Number,
                default: 0
            },
            payload: Object
        },
        data() {
            return {
                left: this.x,
                top: this.y,
                dragging: false
            }
        },
        watch: {
            x(value) {
                this.left = value
            },
            y(value) {
                this.top = value
            }
        },
        methods: {
            onMouseDown(event) {
                if (event.button !== 0) {
                    return
                }
                
                this.dragging = true
                window.addEventListener("mousemove", this.onMouseMove)
                window.addEventListener("mouseup", this.onMouseUp)
                this.$emit("drag-start", {x: this.left, y: this.top, payload: this.payload})
                
                // console.log("event.target:" + event.target.outerHTML)
                // console.log("event.target.offsetWidth:" + event.target.offsetWidth)
                // console.log("event.target.clientWidth:" + event.target.clientWidth)
                // console.log("event.target.scrollWidth:" + event.target.scrollWidth)
            },
            onMouseMove(event) {
                if (!this.dragging) {
                    return
                }
                
                event.stopPropagation();
                
                this.left = this.left + event.movementX
                this.top = this.top + event.movementY
                this.$emit("dragging", {x: this.left, y: this.top, payload: this.payload})
            },
            onMouseUp(event) {
                if (!this.dragging) {
                    return
                }
                
                event.preventDefault();
                
                this.dragging = false
                window.removeEventListener("mousemove", this.onMouseMove)
                window.removeEventListener("mouseup", this.onMouseUp)
                
                this.$emit("drag-end", {x: this.left, y: this.top, payload: this.payload})
                
                // console.log("event.target:" + event.target.outerHTML)
            }
        }
    }
</script>

<style scoped>
    .draggable {
        position: absolute;
    }
    
    .draggable:hover {
        cursor: move;
    }
    
    .draggable > div {
        min-width: 150px;
        height: 50px;
        background-color: #99ccff;
        text-align: center;
        line-height: 50px;
        border: 1px solid #98a5e9;
        border-radius: 5px;
        user-select: none;
    }
</style>