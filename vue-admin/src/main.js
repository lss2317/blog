import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import axios from "axios";
//阿里巴巴icon矢量图CSS引入
import './assets/font/iconfont.css'
//全局样式
import './assets/css/global.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
//引入element-plus的icon图标
import * as ElIconModules from '@element-plus/icons-vue'
//引入NProgress,顶部线条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

const app = createApp(App)

//引入图标组件
for (let iconName in ElIconModules) {
    app.component(iconName, ElIconModules[iconName])
}

axios.interceptors.request.use(config => {
    if (localStorage.getItem("admin_login_token")) {
        config.headers['token'] = localStorage.getItem("admin_login_token")
    }
    if (localStorage.getItem("admin_uuid_token")) {
        config.headers['uuidToken'.toLowerCase()] = localStorage.getItem("admin_uuid_token")
    }
    config.headers['style'] = "admin"
    return config
})

axios.interceptors.response.use(response => {
    if (response.data.code === 1000) {
        localStorage.setItem("message", "长时间未操作,请重新登录")
        window.location.href = '/login'
    }
    return response
})

NProgress.configure({
    easing: "ease", // 动画方式
    speed: 500, // 递增进度条的速度
    showSpinner: false, // 是否显示加载ico
    trickleSpeed: 200, // 自动递增间隔
    minimum: 0.3 // 初始化时的最小百分比
});

router.beforeEach(((to, from, next) => {
    NProgress.start()
    if (!localStorage.getItem("admin_login_token") && to.path !== '/login') {
        next('/login')
    } else {
        next()
    }
}))

router.afterEach(() => {
    NProgress.done()
})

app.use(router)
    .use(store)
    .use(ElementPlus)
    .mount('#app')


