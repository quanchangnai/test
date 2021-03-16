<template>
    <div class="draggable"
         :style="{left: left + 'px', top:top + 'px'}"
         @mousedown.prevent="onDown">
        <slot>
            <div>测试</div>
        </slot>
    </div>
</template>

<script>
    export default {
        name: "Draggable",
        data() {
            return {
                left: 0,
                top: 0,
                dragging: false
            }
        },
        methods: {
            onDown(event) {
                if (event.button === 0) {//鼠标左键
                    this.dragging = true
                    window.onmousemove = this.onMove
                    window.onmouseup = this.onUp
                }
            },
            onMove(event) {
                // event.preventDefault()
                if (this.dragging) {
                    this.left = this.left + event.movementX
                    this.top = this.top + event.movementY
                }
            },
            onUp(event) {
                event.preventDefault()
                this.dragging = false
                window.onmousemove = null
                window.onmouseup = null
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
        width: 100px;
        height: 50px;
        background-color: #99ccff;
        text-align: center;
        line-height: 50px;
    }
</style>