<template>
  <v-dialog v-model="emailFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="emailFlag = false">
        mdi-close
      </v-icon>
      <div class="login-wrapper">
        <!-- 用户名 -->
        <v-text-field
            v-model="email"
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
        <!-- 按钮 -->
        <v-btn
            class="mt-7"
            block
            color="blue"
            style="color:#fff"
            @click="saveUserEmail"
        >
          绑定
        </v-btn>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  data: function () {
    return {
      email: "",
      tempEmail: this.$store.state.email,
      code: "",
      flag: true,
      codeMsg: "发送",
      time: 60,
      show: false
    };
  },
  methods: {
    sendCode() {
      console.log(this.email)
      console.log(this.tempEmail)
      if (this.email === this.tempEmail) {
        this.$toast({
          type: "error",
          message: "不能和旧邮箱一样"
        })
        return false
      }
      const that = this;
      // eslint-disable-next-line no-undef
      // let captcha = new TencentCaptcha(this.config.TENCENT_CAPTCHA, function (
      //     res
      // ) {
      //   if (res.ret === 0) {
          //发送邮件
          that.countDown();
          that.axios
              .get("/api/user/code", {
                params: {username: that.email}
              })
              .then(res => {
                if (res.data.code === 200) {
                  that.$toast({type: "success", message: "发送成功,请注意查看"});
                } else {
                  that.$toast({type: "error", message: "邮件发送失败,请稍后再尝试"});
                }
              });
      //   }
      // });
      // // 显示验证码
      // captcha.show();
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
    saveUserEmail() {
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      if (!reg.test(this.email)) {
        this.$toast({type: "error", message: "邮箱格式不正确"});
        return false;
      }
      if (this.code.trim().length !== 6) {
        this.$toast({type: "error", message: "请输入6位验证码"});
        return false;
      }
      const user = {
        id: this.$store.state.userId,
        email: this.email,
        code: this.code
      };
      this.axios.post("/api/user/changeEmail", user).then(res => {
        if (res.data.code === 200) {
          this.$store.commit("saveEmail", this.email);
          this.email = "";
          this.code = "";
          this.$store.commit("closeModel");
          this.$toast({type: "success", message: res.data.message});
        } else {
          this.$toast({type: "error", message: res.data.message});
        }
      });
    }
  },
  computed: {
    emailFlag: {
      set(value) {
        this.$store.state.emailFlag = value;
      },
      get() {
        return this.$store.state.emailFlag;
      }
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      return clientWidth <= 960;

    }
  },
  watch: {
    email(value) {
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      this.flag = !reg.test(value);
    }
  }
};
</script>

<style scoped>
@media (min-width: 760px) {
  .login-container {
    padding: 1rem;
    border-radius: 4px;
    height: 400px;
  }
}
</style>
