import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vuetify from "./plugins/vuetify";
import animated from "animate.css";
import "./assets/css/index.css";
import "./assets/css/iconfont.css";
import "./assets/css/markdown.css";
import config from "./assets/js/config";
import Share from "vue-social-share";
import "./assets/css/vue-social-share/client.css"
import {vueBaberrage} from "vue-baberrage";
import axios from "axios";
import VueAxios from "vue-axios";
import moment from "moment";
import InfiniteLoading from "vue-infinite-loading";
import "highlight.js/styles/atom-one-dark.css";
import VueImageSwipe from "vue-image-swipe";
import "vue-image-swipe/dist/vue-image-swipe.css";
import Toast from "./components/toast/index";
import NProgress from "NProgress";
import "NProgress/nprogress.css";

Vue.prototype.config = config;
Vue.config.productionTip = false;
Vue.use(animated);
Vue.use(Share);
Vue.use(vueBaberrage);
Vue.use(InfiniteLoading);
Vue.use(VueAxios, axios);
Vue.use(VueImageSwipe);
Vue.use(Toast);

Vue.filter("date", function (value) {
    return moment(value).format("YYYY-MM-DD");
});

Vue.filter('hour',function (value){
    return moment(value).format("HH:mm:ss")
});

Vue.filter("num", function (value) {
    if (value >= 1000) {
        return (value / 1000).toFixed(1) + "k";
    }
    return value;
});


axios.interceptors.request.use(config => {
        if (localStorage.getItem("login_request_token")) {
            config.headers['token'] = localStorage.getItem("login_request_token")
        }
        return config
    }
)

Vue.filter("year", function (value) {
    return moment(value).format("YYYY")
});

router.beforeEach((to, from, next) => {
    NProgress.start();
    if (to.meta.title) {
        document.title = to.meta.title;
    }
    next();
});

router.afterEach(() => {
    window.scrollTo({
        top: 0,
        behavior: "instant"
    });
    NProgress.done();
});

new Vue({
    router,
    store,
    vuetify,
    render: h => h(App)
}).$mount("#app");
