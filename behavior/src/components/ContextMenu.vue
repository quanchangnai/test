<template>
    <div v-if="visible"
         class="context-menu"
         ref="contextMenu"
         :style="{left:x+'px',top:y+'px'}"
         @mouseover="mouseover=true"
         @mouseout="mouseover=false"
         @mousedown.stop>
        <div v-for="(item,index) in items"
             :key="'item'+index"
             class="context-menu-item"
             @click="()=>onItemClick(index)">
            {{ item }}
        </div>
    </div>
</template>

<script>

export default {
    name: "ContextMenu",
    props: {
        items: {
            type: [],
            default: function () {
                return ["测试1", "测试2"]
            }
        }
    },
    data() {
        return {
            visible: false,
            x: 0,
            y: 0,
            mouseover: false
        }
    },
    methods: {
        show(x, y) {
            this.visible = true;
            this.x = x;
            this.y = y;
            window.addEventListener("mousedown", this.tryHide, {capture: true});
            window.addEventListener("resize", this.hide, {once: true});
        },
        tryHide() {
            if (!this.mouseover) {
                this.hide();
            }
        },
        hide() {
            this.visible = false;
            window.removeEventListener("mousedown", this.tryHide);
        },
        onItemClick(index) {
            this.hide();
            this.$emit("item-click", index);
        }
    }
}
</script>

<style scoped>
.context-menu {
    position: fixed;
    z-index: 1000;
    min-width: 80px;
    min-height: 25px;
    line-height: 25px;
    user-select: none;
    cursor: pointer;
    border: solid #dadce0 1px;
    background-color: #ecf5ff;
}

.context-menu-item {
    padding-left: 10px;
    padding-right: 10px;
}

.context-menu-item:hover {
    background-color: #88bef5;
}
</style>