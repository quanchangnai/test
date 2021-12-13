<template>
    <draggable :x="node.x"
               :y="node.y"
               :init-dragging="temp"
               :style="{'pointer-events':temp?'none':'auto'}"
               @drag-start="onDragStart"
               @dragging="onDragging"
               @drag-end="onDragEnd">
        <template>
            <div v-if="temp" class="content" ref="content">
                tid:{{ node.tid }},id:{{ node.id }}
            </div>
            <div v-else class="content" ref="content">
                <div style="padding-right: 5px">
                    <span>节点:{{ node.tid }}-{{ node.id }}</span>
                </div>
                <div v-if="node.detailed" style="border-top: solid cadetblue 1px;">
                    参数1:aaaaaaaaa{{ node.id }}
                </div>
                <div v-if="node.detailed">
                    参数2:bbbbbbbbb{{ node.id }}
                </div>
                <div v-if="node.detailed">
                    参数3:cccccccc{{ node.id }}
                </div>
            </div>
            <div :title="node.detailed?'隐藏节点详情':'显示节点详情'"
                 class="detail-icon"
                 :class="node.detailed?'el-icon-arrow-up':'el-icon-arrow-down'"
                 @mousedown.stop
                 @click="onDetail"/>
            <div v-if="node.children&&node.children.length"
                 :title="node.collapsed?'展开子树':'收起子树'"
                 class="collapse-icon"
                 :class="node.collapsed?'el-icon-circle-plus-outline':'el-icon-remove-outline'"
                 @mousedown.stop
                 @click="onCollapse"/>
        </template>
    </draggable>
</template>

<script>
import Draggable from './Draggable'

export default {
    name: "TreeNode",
    components: {Draggable},
    props: {
        node: Object,
        temp: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {};
    },
    methods: {
        onDragStart() {
            this.node.dragging = true;
            this.$emit("drag-start", {node: this.node});
        },
        onDragging(event) {
            const deltaX = event.x - this.node.x;
            const deltaY = event.y - this.node.y;

            const move = node0 => {
                node0.x += deltaX;
                node0.y += deltaY;
                if (node0.children) {
                    for (let child of node0.children) {
                        move(child);
                    }
                }
            };

            move(this.node);

            this.$emit("dragging", {node: this.node});
        },
        onDragEnd() {
            this.node.dragging = false;
            this.$emit("drag-end", {node: this.node});
        },
        onDetail() {
            this.$set(this.node, "detailed", !this.node.detailed);
            this.$emit("detail");
        },
        onCollapse() {
            this.$set(this.node, "collapsed", !this.node.collapsed);
            this.$emit("collapse");
        },
        content() {
            return this.$refs.content;
        }
    }

}
</script>

<style scoped>
.content {
    min-width: 60px;
    background-color: #99ccff;
    line-height: 30px;
    border: 1px solid #98a5e9;
    border-radius: 5px;
    padding: 0 10px 0 23px;
}

.content:hover {
    cursor: pointer;
    background-color: #00981a;
}

.detail-icon {
    position: absolute;
    top: 7px;
    left: 5px;
    cursor: default
}

.collapse-icon {
    position: absolute;
    top: calc(50% - 7px);
    left: calc(100% - 1px);
    cursor: default
}
</style>