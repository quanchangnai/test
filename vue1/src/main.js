import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import routes from './routes'

Vue.config.productionTip = false
Vue.use(VueRouter)
Vue.use(ElementUI)

const router = new VueRouter({routes});

new Vue({
    // eslint-disable-next-line no-unused-vars
    render(h) {
        return h(App)
    },
    router,
    created() {
        console.info(this)
    }
}).$mount('#app')
