<template>
    <el-table :data="visibleTemplates" size="medium" stripe border>
        <el-table-column>
            <template #header>
                <el-input v-model="keyword"
                          clearable
                          size="medium"
                          placeholder="输入关键字搜索"
                          prefix-icon="el-icon-search"/>
            </template>
            <template #default="{row}">
                <div style="cursor: grab;user-select: none;" @mousedown="e=>onTemplateSelect(e,row)"> {{ row.name }}</div>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
import {ipcRenderer} from "electron";

export default {
    name: "TemplateList",
    async mounted() {
        this.allTemplates = await ipcRenderer.invoke("load-templates");
        this.visibleTemplates = this.allTemplates;
    },
    data() {
        return {
            allTemplates: null,
            visibleTemplates: null,
            keyword: ""
        }
    },
    watch: {
        keyword(value) {
            this.visibleTemplates = this.allTemplates.filter(template => {
                return template.name.includes(value);
            });
        }
    },
    methods: {
        onTemplateSelect(event, template) {
            this.$emit("template-select", {x: event.clientX, y: event.clientY, template});
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