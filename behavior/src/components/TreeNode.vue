<template>
    <draggable :x="node.x"
               :y="node.y"
               :init-dragging="temp"
               :style="{'pointer-events':temp?'none':'auto'}"
               @drag-start="onDragStart"
               @dragging="onDragging"
               @drag-end="onDragEnd"
               @contextmenu.native.stop="onContextMenu">
        <template>
            <div v-if="temp" class="content" ref="content">
                tid:{{ node.tid }},id:{{ node.id }}
            </div>
            <div v-else class="content" ref="content">
                <div style="padding-right: 5px">
                    <span>节点:{{ node.tid }}-{{ node.id }}</span>
                </div>
                <template v-if="node.detailed">
                    <div style="border-top: solid cadetblue 1px;">
                        参数1:aaaaaaaaa{{ node.id }}
                    </div>
                    <div>
                        参数2:bbbbbbbbb{{ node.id }}
                    </div>
                    <div>
                        参数3:cccccccc{{ node.id }}
                    </div>
                </template>
            </div>
            <div class="detail-icon"
                 :class="node.detailed?'el-icon-arrow-up':'el-icon-arrow-down'"
                 @mousedown.stop
                 @click="onDetail"/>
            <div v-if="node.children&&node.children.length"
                 class="collapse-icon"
                 :class="node.collapsed?'el-icon-circle-plus-outline':'el-icon-remove-outline'"
                 @mousedown.stop
                 @click="onCollapse"/>
            <context-menu ref="contextMenu" :items="menuItems"/>
        </template>
    </draggable>
</template>

<script>
import Draggable from './Draggable'
import ContextMenu from './ContextMenu'

export default {
    name: "TreeNode",
    components: {Draggable, ContextMenu},
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
    computed: {
        menuItems() {
            let items = [];
            items.push({title: this.node.detailed ? '隐藏详情' : '显示详情', handler: this.onDetail});
            if (this.node.children && this.node.children.length) {
                items.push({title: this.node.collapsed ? '展开子树' : '收起子树', handler: this.onCollapse});
            }
            items.push({title: '删除节点', handler: this.onDelete});
            return items;
        }
    },
    methods: {
        content() {
            return this.$refs.content;
        },
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

            this.$emit("dragging", this.node);
        },
        onDragEnd() {
            this.node.dragging = false;
            this.$emit("drag-end", this.node);
        },
        onDetail() {
            this.$set(this.node, "detailed", !this.node.detailed);
            this.$emit("detail");
        },
        onCollapse() {
            this.$set(this.node, "collapsed", !this.node.collapsed);
            this.$emit("collapse");
        },
        onDelete() {
            this.$emit("delete", this.node);
        },
        onContextMenu(event) {
            this.$refs.contextMenu.show(event.clientX, event.clientY);
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
    z-index: 10;
}

.content:hover {
    cursor: grab;
}

.detail-icon {
    position: absolute;
    top: 0;
    left: 0;
    width: 20px;
    height: 24px;
    padding-top: 7px;
    padding-left: 4px;
    cursor: pointer;
}

.collapse-icon {
    position: absolute;
    top: calc(50% - 7px);
    left: calc(100% - 1px);
    cursor: pointer;
}
</style>