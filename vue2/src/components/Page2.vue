<template>
    <div style="width: 100%;height: 100%" @contextmenu.prevent="onContextMenu" @mouseup="onMouseUp">
        显示:<input type="checkbox" v-model="show"/>
        <input type="button"  @click="onButtonClick" value="按钮"/>
        <div id="bbb"  v-if="show" style="z-index: 10;background-color: aliceblue">bbb</div>
        <context-menu ref="menu"/>
    </div>
</template>

<script>
import ContextMenu from "./ContextMenu";
export default {
    name: "Page2",
    components: {ContextMenu},
    data() {
        return {show: true}
    },
    methods: {
        onButtonClick() {
            this.show=true;
            this.$nextTick(()=>{
                let element = document.querySelector("#bbb");
                console.log("onButtonClick,show:" + this.show+",element:"+element);
                document.body.appendChild(element);
            })
        },
        onContextMenu(event) {
            this.$refs.menu.show(event.clientX, event.clientY);
        },
        onMouseUp() {
            this.$refs.menu.hide();
        }
    }
}
</script>

<style scoped>

</style>