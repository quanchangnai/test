<template>
    <el-table border
              ref="table"
              size="medium"
              :height="'100%'"
              :data="visibleTrees"
              highlight-current-row
              @current-change="onTreeSelect">
        <el-table-column>
            <template #header>
                <el-input v-model="keyword"
                          clearable
                          size="medium"
                          placeholder="输入关键字搜索"
                          prefix-icon="el-icon-search"/>
            </template>
            <template #default="{row}">
                <div> {{ row.name }}</div>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
import {ipcRenderer} from 'electron'

export default {
    name: "TreeList",
    data() {
        return {
            allTrees: null,
            visibleTrees: null,
            selectedTree: null,
            keyword: ""
        }
    },
    async mounted() {
        this.allTrees = await ipcRenderer.invoke("load-trees");
        this.visibleTrees = this.allTrees;
        this.$refs.table.setCurrentRow(this.visibleTrees[0]);
        this.onTreeSelect(this.visibleTrees[0]);
    },
    watch: {
        keyword(value) {
            this.visibleTrees = this.allTrees.filter(tree => {
                return tree === this.selectedTree || tree.name.includes(value);
            });
        }
    },
    methods: {
        onTreeSelect(tree) {
            this.selectedTree = tree;
            this.$emit("tree-select", tree);
        }
    }
}
</script>

<style scoped>
/*noinspection CssUnusedSymbol*/
.el-table {
    height: 100%;
}
</style>