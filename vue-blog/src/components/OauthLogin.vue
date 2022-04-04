<template>
  <div class="oauth-background">
    <div id="preloader_1">
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
    </div>
  </div>
</template>

<script>
/* eslint-disable no-undef */
export default {
  created() {
    const that = this;
    //关闭登录框
    that.$store.state.loginFlag = false;
    //通过路径判断是微博登录还是qq登录
    if (that.$route.path === "/qq/myback") {
      // 拿到openId，accessToken传入后台
      if (QC.Login.check()) {
        QC.Login.getMe(function (openId, accessToken) {
          that.axios
              .post("/api/user/qq/login", {
                openId: openId,
                accessToken: accessToken
              })
              .then(res => {
                if (res.data.code === 200) {
                  //保存登录状态
                  that.$store.commit("login", res.data.data);
                  that.$toast({type: "success", message: res.data.message});
                  window.localStorage.setItem("login_request_token", res.data.data.ipAddress)
                } else {
                  that.$toast({type: "error", message: res.data.message});
                }
              });
        });
      } else {
        that.$toast({
          type: "error",
          message: "请登录"
        });
      }
    } else {
      that.axios
          .post("/api/user/weibo/login", {
            code: this.$route.query.code
          })
          .then(res => {
            if (res.data.code === 200) {
              //保存登录状态
              that.$store.commit("login", res.data.data);
              that.$toast({type: "success", message: res.data.message});
              window.localStorage.setItem("login_request_token", res.data.data.ipAddress)
            } else {
              that.$toast({type: "error", message: res.data.message});
            }
          });
    }
    // 跳转回原页面
    const loginUrl = that.$store.state.loginUrl;
    if (loginUrl != null && loginUrl !== "") {
      that.$router.push({path: loginUrl});
    } else {
      that.$router.push({path: "/"});
    }
  }
};
</script>

<style scoped>
.oauth-background {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 1000;
}

#preloader_1 {
  position: relative;
  top: 45vh;
  left: 45vw;
}

#preloader_1 span {
  display: block;
  bottom: 0px;
  width: 9px;
  height: 5px;
  background: #9b59b6;
  position: absolute;
  animation: preloader_1 1.5s infinite ease-in-out;
}

#preloader_1 span:nth-child(2) {
  left: 11px;
  animation-delay: 0.2s;
}

#preloader_1 span:nth-child(3) {
  left: 22px;
  animation-delay: 0.4s;
}

#preloader_1 span:nth-child(4) {
  left: 33px;
  animation-delay: 0.6s;
}

#preloader_1 span:nth-child(5) {
  left: 44px;
  animation-delay: 0.8s;
}

@keyframes preloader_1 {
  0% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
  25% {
    height: 30px;
    transform: translateY(15px);
    background: #3498db;
  }
  50% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
  100% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
}
</style>
