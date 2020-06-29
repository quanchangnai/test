<template>
    <div v-on:click="onContentClick">
        <span v-if="show">{{content}}</span>
        <span v-else>aaa</span>
    </div>
</template>

<script>
    export default {
        name: "Page1",
        data() {
            return {
                content: "AAA",
                show: true
            }
        },
        methods: {
            onContentClick() {
                this.show = !this.show
            }
        }
    }
</script>

<style scoped>

</style>