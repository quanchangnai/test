<template>
    <el-menu :mode="mode" :default-active="activeIndex" router @select="onSelect" >
        <el-menu-item index="1" route="aaa">菜单项a1</el-menu-item>
        <el-menu-item index="2" route="bbb">菜单项b2</el-menu-item>
        <el-menu-item index="3" route="ccc">菜单项c</el-menu-item>
    </el-menu>
</template>

<script>
    export default {
        name: "Menu",
        data() {
            return {
                activeIndex: "1"
            }
        },
        props: {
            mode: {
                type: String,
                default: 'horizontal'
            }
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