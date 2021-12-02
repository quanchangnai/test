<template>
    <el-menu :mode="mode"
             :default-active="defaultItem"
             background-color="#545c64"
             text-color="#fff"
             active-text-color="#409eff"
             @select="onSelect"
             ref="menu"
             router>
        <el-menu-item index="/aaa">菜单项a1</el-menu-item>
        <el-menu-item index="/bbb">菜单项b2</el-menu-item>
        <el-menu-item index="/ccc/3?limit=3">菜单项c3</el-menu-item>
    </el-menu>
</template>

<script>
export default {
    name: "Menu",
    data() {
        return {
            defaultItem: ""
        }
    },
    props: {
        mode: {
            type: String,
            default: 'horizontal'
        }
    },
    created() {
        this.defaultItem = this.$route.fullPath
    },
    methods: {
        onSelect(index) {
            // this.$router.push(index)
            console.log("selected:" + index);
            console.log("defaultItem:" + this.defaultItem);
        }
    }
}
</script>

<style scoped>

</style>