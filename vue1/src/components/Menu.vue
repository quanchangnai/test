<template>
    <div>
        <el-menu :mode="mode"
                 :default-active="activeIndex"
                 background-color="#545c64"
                 text-color="#fff"
                 active-text-color="#409eff"
                 @select="onSelect"
                 ref="menu"
                 router>
            <el-menu-item index="aaa" route="aaa">菜单项a1</el-menu-item>
            <el-menu-item index="bbb" route="bbb">菜单项b2</el-menu-item>
            <el-menu-item index="ccc" route="ccc">菜单项c</el-menu-item>
        </el-menu>
    </div>
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