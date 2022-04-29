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
    <!--音乐播放器-->
    <player style="z-index: 100000" v-if="blogInfo.websiteConfig.isMusicPlayer === 1 && !isMobile"></player>
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
import player from "./components/zw-player/player";


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
    ChatRoom,
    player
  },
  created() {
    this.getBlogInfo();
    this.axios.get("/api/blogInfo/report")
    if (this.$store.state.userId === null) {
      window.localStorage.removeItem("login_request_token")
    } else {
      this.axios.post("/api/blogInfo/getLikes").then(res => {
        console.log(res.data)
        if (res.data.talkLikeSet !== null) {
          this.$store.state.talkLikeSet = res.data.talkLikeSet
        }
        if (res.data.commentLikeSet !== null) {
          this.$store.state.commentLikeSet = res.data.commentLikeSet
        }
        if (res.data.articleLikeSet !== null) {
          this.$store.state.articleLikeSet = res.data.articleLikeSet
        }
      })
    }
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
  },
};
</script>
