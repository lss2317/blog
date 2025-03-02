<template>
  <v-dialog v-model="loginFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="closeLoginFlag">
        mdi-close
      </v-icon>
      <div class="login-wrapper">
        <!-- 用户名 -->
        <v-text-field
            v-model="username"
            label="邮箱号"
            placeholder="请输入您的邮箱号"
            clearable
            @keyup.enter="login"
        />
        <!-- 密码 -->
        <v-text-field
            v-model="password"
            class="mt-7"
            label="密码"
            placeholder="请输入您的密码"
            @keyup.enter="login"
            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            :type="show ? 'text' : 'password'"
            @click:append="show = !show"
        />
        <!-- 验证码组件-->
        <AliyunCaptcha
            v-if="isCaptchaVisible"
            @success="onBizResultCallback"
        />
        <!-- 按钮 -->
        <v-btn
            class="mt-7"
            block
            color="blue"
            style="color:#fff"
            @click="login"
        >
          登录
        </v-btn>
        <!-- 注册和找回密码 -->
        <div class="mt-10 login-tip">
          <span @click="openRegister">立即注册</span>
          <span @click="openForget" class="float-right">忘记密码?</span>
        </div>
        <div class="social-login-title">社交账号登录</div>
        <div class="social-login-wrapper">
          <!-- 微博登录 -->
          <a
              @click="weiboLogin"
              class="mr-3 iconfont iconweibo"
              style="color:#e05244"
          />
          <!-- qq登录 -->
          <a @click="qqLogin" class="iconfont iconqq" style="color:#00AAEE"/>
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import AliyunCaptcha from './AliyunCaptcha.vue';

export default {
  components: {
    AliyunCaptcha
  },
  data: function () {
    return {
      username: "",
      password: "",
      show: false,
      isCaptchaVisible: false
    };
  },
  computed: {
    loginFlag: {
      set(value) {
        this.$store.state.loginFlag = value;
      },
      get() {
        return this.$store.state.loginFlag;
      }
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      return clientWidth <= 960;
    }
  },
  methods: {
    onBizResultCallback() {
      const that = this;
      let param = {
        username: this.username,
        password: this.password
      };
      that.axios.post("/api/user/login", param).then(res => {
        if (res.data.code === 200) {
          that.username = "";
          that.password = "";
          that.$store.commit("login", res.data.data);
          that.$store.commit("closeModel");
          that.$toast({type: "success", message: res.data.message});
          window.localStorage.setItem("login_request_token", res.data.data.ipAddress)
        } else {
          that.$toast({type: "error", message: res.data.message});
        }
      });
    },
    openRegister() {
      this.$store.state.loginFlag = false;
      this.$store.state.registerFlag = true;
    },
    openForget() {
      this.$store.state.loginFlag = false;
      this.$store.state.forgetFlag = true;
    },
    login() {
      if (this.username.length === 0) {
        this.$toast({type: "error", message: "用户名不能为空"});
        return false;
      }
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      if (!reg.test(this.username)) {
        this.$toast({type: "error", message: "邮箱格式不正确"});
        return false;
      }
      if (this.password.trim().length === 0) {
        this.$toast({type: "error", message: "密码不能为空"});
        return false;
      }
      if (!this.isCaptchaVisible) {
        this.isCaptchaVisible = true //显示验证码组件
      } else {
        this.isCaptchaVisible = false; // 先隐藏验证码组件
        this.$nextTick(() => {
          this.isCaptchaVisible = true; // 重新显示验证码组件
        });
      }
    },
    qqLogin() {
      //保留当前路径
      this.$store.commit("saveLoginUrl", this.$route.path);
      if (
          navigator.userAgent.match(
              /(iPhone|iPod|Android|ios|iOS|iPad|Backerry|WebOS|Symbian|Windows Phone|Phone)/i
          )
      ) {
        // eslint-disable-next-line no-undef
        QC.Login.showPopup({
          appId: this.config.QQ_APP_ID,
          redirectURI: this.config.QQ_REDIRECT_URI
        });
      } else {
        window.open(
            "https://graph.qq.com/oauth2.0/show?which=Login&display=pc&client_id=" +
            +this.config.QQ_APP_ID +
            "&response_type=token&scope=all&redirect_uri=" +
            this.config.QQ_REDIRECT_URI,
            "_self"
        );
      }
    },
    weiboLogin() {
      //保留当前路径
      this.$store.commit("saveLoginUrl", this.$route.path);
      window.open(
          "https://api.weibo.com/oauth2/authorize?client_id=" +
          this.config.WEIBO_APP_ID +
          "&response_type=code&redirect_uri=" +
          this.config.WEIBO_REDIRECT_URI,
          "_self"
      );
    },
    closeLoginFlag() {
      this.$store.commit("closeModel");
    }
  }
};
</script>

<style scoped>
.social-login-title {
  margin-top: 1.5rem;
  color: #b5b5b5;
  font-size: 0.75rem;
  text-align: center;
}

.social-login-title::before {
  content: "";
  display: inline-block;
  background-color: #d8d8d8;
  width: 60px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}

.social-login-title::after {
  content: "";
  display: inline-block;
  background-color: #d8d8d8;
  width: 60px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}

.social-login-wrapper {
  margin-top: 1rem;
  font-size: 2rem;
  text-align: center;
}

.social-login-wrapper a {
  text-decoration: none;
}
</style>
