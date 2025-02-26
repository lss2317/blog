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
      }
    }
  },
  methods: {
    login() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          const that = this;
          // eslint-disable-next-line no-undef
          // const captcha = new TencentCaptcha(
          //     config.TENCENT_CAPTCHA,
          //     function (res) {
          //       if (res.ret === 0) {
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
          //       }
          //     }
          // );
          // // 显示验证码
          // captcha.show();
        } else {
          return false;
        }
      });
    }
  },
  created() {
    if (localStorage.getItem("message")) {
      this.$message.error(localStorage.getItem("message"))
      localStorage.removeItem("message")
      localStorage.removeItem("admin_login_token")
      localStorage.removeItem("admin_uuid_token")
      localStorage.removeItem("userInfo")
    }
  }
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
