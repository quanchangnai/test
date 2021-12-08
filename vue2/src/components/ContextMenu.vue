<template>
    <div id="content" v-if="visible" :style="{left:x+'px',top:y+'px'}" @click.stop>
        右键菜单1
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
    height: 100px;
    background-color: darkturquoise;
}
</style>