<template>
  <v-app id="app">
    <!-- 导航栏 -->
    <TopNavBar></TopNavBar>
    <!-- 侧边导航栏 -->
    <SideNavBar></SideNavBar>
    <!-- 内容 -->
    <v-main>
      <router-view :key="$route.fullPath"></router-view>
    </v-main>
    <!-- 页脚 -->
    <Footer style="z-index: 1"></Footer>
    <!-- 返回顶部 -->
    <BackTop></BackTop>
    <!-- 搜索模态框 -->
    <searchModel></searchModel>
    <!-- 登录模态框 -->
    <LoginModel></LoginModel>
    <!-- 注册模态框 -->
    <RegisterModel></RegisterModel>
    <!-- 忘记密码模态框 -->
    <ForgetModel></ForgetModel>
    <!-- 绑定邮箱模态框 -->
    <EmailModel></EmailModel>

    <!-- 聊天室 -->
    <ChatRoom v-if="blogInfo.websiteConfig.isChatRoom === 1" style="z-index: 100000"></ChatRoom>
  </v-app>
</template>

<script>
import TopNavBar from "./components/layout/TopNavBar";
import SideNavBar from "./components/layout/SideNavBar";
import Footer from "./components/layout/Footer";
import BackTop from "./components/BackTop";
import searchModel from "./components/model/SearchModel";
import LoginModel from "./components/model/LoginModel";
import RegisterModel from "./components/model/RegisterModel";
import ForgetModel from "./components/model/ForgetModel";
import EmailModel from "@/components/model/EmailModel";
import ChatRoom from "@/components/ChatRoom";
import {L2Dwidget} from 'live2d-widget'


export default {
  components: {
    TopNavBar,
    SideNavBar,
    Footer,
    BackTop,
    searchModel,
    LoginModel,
    RegisterModel,
    ForgetModel,
    EmailModel,
    ChatRoom
  },
  created() {
    this.getBlogInfo();
    this.axios.get("/api/blogInfo/report")
    if (this.$store.state.userId === null) {
      window.localStorage.removeItem("login_request_token")
    }
    const Live = document.createElement('script');
    Live.src = "https://eqcn.ajz.miesnfu.com/wp-content/plugins/wp-3d-pony/live2dw/lib/L2Dwidget.min.js";
    //Live.src = "../public/live2dw/lib/L2Dwidget.min.js"
    Live.onload = function () {
      L2Dwidget.init({
        model: {
          jsonPath: 'https://cdn.jsdelivr.net/gh/wangsrGit119/wangsr-image-bucket/L2Dwidget/live2d-widget-model-haruto/assets/haruto.model.json',
          //jsonPath: "../public/live2dw/live2d-widget-model-haruto/assets/haruto.model.json"
        },
        display: {vOffset: 10},  //调整大小,和位置
        dialog: {
          enable: true,
          script: {
            'tap body': '哎呀！别碰我！',
            'tap face': '人家是在认真写博客哦--前端妹子',
          }
        }
      });
    };
    document.body.appendChild(Live);
  },
  methods: {
    getBlogInfo() {
      this.axios.get("/api/blogInfo/getBlogInfo").then(res => {
        this.$store.commit("checkBlogInfo", res.data);
      });
    }
  },
  computed: {
    blogInfo() {
      return this.$store.state.blogInfo;
    },
    isMobile() {
      return navigator.userAgent.match(
          /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
      );
    }
  }
};
</script>
