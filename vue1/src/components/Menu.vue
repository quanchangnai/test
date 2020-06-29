<template>
    <el-menu mode="horizontal" :default-active="activeIndex" router @select="onSelect">
        <el-menu-item index="1" route="aaa" >菜单项a</el-menu-item>
        <el-menu-item index="2" route="bbb">菜单项b</el-menu-item>
        <el-menu-item index="3" route="ccc">菜单项c</el-menu-item>
    </el-menu>
</template>

<script>
    export default {
        name: "Menu",
        data() {
            return {activeIndex: "1"}
        },
        methods: {
            onSelect(index) {
                console.log("selected:" + index);
                console.log("route.path:" + this.$route.path);
            }
        }
    }
</script>

<style scoped>

</style>