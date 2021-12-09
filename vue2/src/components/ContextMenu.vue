<template>
    <div id="content" v-if="visible" :style="{left:x+'px',top:y+'px'}" @click.stop>
        <slot>右键菜单</slot>
    </div>
</template>

<script>

export default {
    name: "ContextMenu",
    data() {
        return {
            visible: false,
            x: 0,
            y: 0
        }
    },
    methods: {
        show(x, y) {
            this.visible = true;
            this.x = x;
            this.y = y;
            this.$nextTick(() => {
                let content = document.querySelector("#content");
                document.body.appendChild(content);
                window.addEventListener("click", this.hide, {once: true});
            })

        },
        hide() {
            this.visible = false;
        }
    }
}
</script>

<style scoped>
#content {
    position: absolute;
    z-index: 1000;
    min-width: 150px;
    min-height: 50px;
    border: solid #dadce0 1px;
    background-color: #ffffff;
}
</style>