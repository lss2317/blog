<template>
  <v-dialog v-model="registerFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="closeRegisterFlag">
        mdi-close
      </v-icon>
      <div class="login-wrapper">
        <!-- 用户名 -->
        <v-text-field
            v-model="username"
            label="邮箱号"
            placeholder="请输入您的邮箱号"
            clearable
            @keyup.enter="register"
        />
        <!-- 验证码 -->
        <div class="mt-7 send-wrapper">
          <v-text-field
              maxlength="6"
              v-model="code"
              label="验证码"
              placeholder="请输入6位验证码"
              @keyup.enter="register"
          />
          <v-btn text small :disabled="flag" @click="sendCode">
            {{ codeMsg }}
          </v-btn>
        </div>
        <!-- 密码 -->
        <v-text-field
            v-model="password"
            class="mt-7"
            label="密码"
            placeholder="请输入您的密码"
            @keyup.enter="register"
            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            :type="show ? 'text' : 'password'"
            @click:append="show = !show"
        />
        <!-- 验证码组件-->
        <AliyunCaptcha
            v-if="isCaptchaVisible"
            @success="onBizResultCallback"
        />
        <!-- 注册按钮 -->
        <v-btn
            class="mt-7"
            block
            color="red"
            style="color:#fff"
            @click="register"
        >
          注册
        </v-btn>
        <!-- 登录 -->
        <div class="mt-10 login-tip">
          已有账号？<span @click="openLogin" style="color: #1f6feb">登录</span>
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import AliyunCaptcha from "@/components/model/AliyunCaptcha";

export default {
  components: {
    AliyunCaptcha
  },
  data: function () {
    return {
      username: "",
      code: "",
      password: "",
      flag: true,
      codeMsg: "发送",
      time: 60,
      show: false,
      isCaptchaVisible: false
    };
  },
  methods: {
    onBizResultCallback() {
      const user = {
        username: this.username,
        password: this.password,
        code: this.code
      };
      this.axios.post("/api/user/register", user).then(res => {
        if (res.data.code === 200) {
          let param = {
            username: user.username,
            password: user.password
          }
          this.axios.post("/api/user/login", param).then(result => {
            this.username = "";
            this.password = "";
            this.code = ""
            this.$store.commit("login", result.data.data);
            window.localStorage.setItem("login_request_token", result.data.data.ipAddress)
            this.$store.commit("closeModel");
          });
          this.$toast({type: "success", message: "登录成功"});
        } else {
          this.$toast({type: "error", message: res.data.message});
        }
      });
    },
    openLogin() {
      this.$store.state.registerFlag = false;
      this.$store.state.loginFlag = true;
    },
    sendCode() {
      const that = this;
      //发送邮件
      that.countDown();
      that.axios
          .get("/api/user/code", {
            params: {username: that.username}
          })
          .then(res => {
            if (res.data.code === 200) {
              that.$toast({type: "success", message: "发送成功,请注意查看"});
            } else {
              that.$toast({type: "error", message: "发送失败,请稍后再试"});
            }
          });
    },
    countDown() {
      this.flag = true;
      this.timer = setInterval(() => {
        this.time--;
        this.codeMsg = this.time + "s";
        if (this.time <= 0) {
          clearInterval(this.timer);
          this.codeMsg = "发送";
          this.time = 60;
          this.flag = false;
        }
      }, 1000);
    },
    register() {
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      let passwordStandard = /^[A-Za-z0-9]+$/;
      if (!reg.test(this.username)) {
        this.$toast({type: "error", message: "邮箱格式不正确"});
        return false;
      }
      if (this.code.trim().length !== 6) {
        this.$toast({type: "error", message: "请输入6位验证码"});
        return false;
      }
      if (this.password.trim().length < 6) {
        this.$toast({type: "error", message: "密码不能少于6位"});
        return false;
      }
      if (this.password.trim().length > 18) {
        this.$toast({type: "error", message: "密码不能长于18位"});
        return false;
      }
      if (!passwordStandard.test(this.password)) {
        this.$toast({type: "error", message: "密码只能由数字和字母组成"})
        return false
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
    closeRegisterFlag() {
      this.$store.commit("closeModel");
    }
  },
  computed: {
    registerFlag: {
      set(value) {
        this.$store.state.registerFlag = value;
      },
      get() {
        return this.$store.state.registerFlag;
      }
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      return clientWidth <= 960;

    }
  },
  watch: {
    username(value) {
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      this.flag = !reg.test(value);
    }
  }
};
</script>
