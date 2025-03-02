<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-title">用户登录</div>
      <!-- 登录表单 -->
      <el-form
          status-icon
          :model="user"
          :rules="rules"
          ref="ruleForm"
          class="login-form"
      >
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input
              v-model="user.username"
              prefix-icon="user"
              size="large"
              placeholder="用户名"
              @keyup.enter.native="login"
          />
        </el-form-item>
        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
              v-model="user.password"
              prefix-icon="lock"
              show-password
              size="large"
              placeholder="密码"
              @keyup.enter.native="login"
          />
        </el-form-item>
      </el-form>
      <!-- 登录按钮 -->
      <div id="captcha-element"></div>
      <el-button type="primary" size="large" @click="login">登录</el-button>
    </div>
  </div>
</template>

<script>
import config from "../../assets/js/config"
import axios from "axios";
import {ElLoading} from "element-plus";
import store from "../../store";

export default {
  name: "Login",
  data() {
    return {
      loading: false,
      user: {
        username: "",
        password: ""
      },
      rules: {
        username: [
          {required: true, message: "用户名不能为空", trigger: "blur"}
        ],
        password: [{required: true, message: "密码不能为空", trigger: "blur"}]
      },
      //验证码实例
      captcha: null,
    }
  },
  methods: {
    login() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          this.captcha.show()
        } else {
          return false;
        }
      });
    },
    getInstance(instance) {
      this.captcha = instance;
    },
    async captchaVerifyCallback(captchaVerifyParam) {
      console.log(captchaVerifyParam);
      //向后端发起业务请求，获取验证码验证结果和业务结果
      const res = await axios.post("/api/user/CheckVerificationCode", {
        captchaVerifyParam: captchaVerifyParam
      });
      const flag = res.data.toString() === "true"; // 将字符串转换为布尔值
      if (flag) {
        return {
          captchaResult: true, // 验证码验证是否通过，boolean类型，必选
          bizResult: true, // 业务验证是否通过，boolean类型，可选；若为无业务验证结果的场景，bizResult可以为空
        }
      } else {
        return {
          captchaResult: false,
          bizResult: false,
        }
      }
    },
    // 验证通过后调用
    onBizResultCallback() {
      //登录
      const that = this;
      let loading = ElLoading.service({
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.7)',
      })
      //发送登录请求
      axios.post("/api/user/admin/login", that.user).then(response => {
        if (response.data.code === 200) {
          that.$message.success(response.data.message)
          window.localStorage.setItem("admin_login_token", response.data.data.token)
          window.localStorage.setItem("admin_uuid_token", response.data.data.uuid)
          store.commit("changeAvatar", response.data.data.user)
          loading.close()
          that.$router.push("/")
        } else if (response.data.code === 500) {
          loading.close()
          that.$message.error(response.data.message)
        } else if (response.data.code === 300) {
          loading.close()
          that.$message.error(response.data.message)
        }
      })
    },
  },
  created() {
    if (localStorage.getItem("message")) {
      this.$message.error(localStorage.getItem("message"))
      localStorage.removeItem("message")
      localStorage.removeItem("admin_login_token")
      localStorage.removeItem("admin_uuid_token")
      localStorage.removeItem("userInfo")
    }
  },
  mounted() {
    window.initAliyunCaptcha({
      SceneId: config.ALIYUN_SceneId, // 场景ID。根据步骤二新建验证场景后，您可以在验证码场景列表，获取该场景的场景ID
      prefix: config.ALIYUN_PREFIX, // 身份标。开通阿里云验证码2.0后，您可以在控制台概览页面的实例基本信息卡片区域，获取身份标
      mode: 'popup', // 验证码模式。popup表示要集成的验证码模式为弹出式。无需修改
      element: '#captcha-element', // 页面上预留的渲染验证码的元素，与原代码中预留的页面元素保持一致。
      captchaVerifyCallback: this.captchaVerifyCallback, // 业务请求(带验证码校验)回调函数，无需修改
      onBizResultCallback: this.onBizResultCallback, // 业务请求结果回调函数，无需修改
      getInstance: this.getInstance, // 绑定验证码实例函数，无需修改
      slideStyle: {
        width: 360,
        height: 40,
      }, // 滑块验证码样式，支持自定义宽度和高度，单位为px。其中，width最小值为320 px
      language: 'cn', // 验证码语言类型，支持简体中文（cn）、繁体中文（tw）、英文（en）
    });
  },
}
</script>

<style scoped>
.login-container {
  position: absolute;
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  background: url(https://blog-1307541812.cos.ap-shanghai.myqcloud.com/0w3pdr.jpg) center center /
    cover no-repeat;
}

.login-card {
  position: absolute;
  top: 0;
  bottom: 0;
  right: 0;
  background: #fff;
  padding: 170px 60px 180px;
  width: 350px;
}

.login-title {
  color: #303133;
  font-weight: bold;
  font-size: 1rem;
}

.login-form {
  margin-top: 1.2rem;
}

.login-card button {
  margin-top: 1rem;
  width: 100%;
}
</style>
