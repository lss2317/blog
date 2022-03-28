<template>
  <el-card class="main-card">
    <el-tabs v-model="activeName">
      <!-- 修改信息 -->
      <el-tab-pane label="修改信息" name="info">
        <div class="info-container">
          <el-upload
              class="avatar-uploader"
              action="/api/files/upload"
              :show-file-list="false"
              :on-success="updateAvatar"
          >
            <img v-if="this.infoForm.avatar !== ''" :src="this.infoForm.avatar" class="avatar"/>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
          <el-form
              label-width="70px"
              :model="infoForm"
              style="width:320px;margin-left:3rem"
          >
            <el-form-item label="昵称">
              <el-input v-model="infoForm.nickname"/>
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="infoForm.intro"/>
            </el-form-item>
            <el-form-item label="个人网站">
              <el-input v-model="infoForm.website"/>
            </el-form-item>
            <el-button
                @click="updateInfo"
                type="primary"
                style="margin-left:4.375rem"
            >
              修改
            </el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-form label-width="70px" :model="passwordForm" style="width:320px">
          <el-form-item label="旧密码" class="form">
            <el-input
                @keyup.enter.native="updatePassword"
                v-model="passwordForm.oldPassword"
                show-password
            />
          </el-form-item>
          <el-form-item label="新密码" class="form">
            <el-input
                @keyup.enter.native="updatePassword"
                v-model="passwordForm.newPassword"
                show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" class="form">
            <el-input
                @keyup.enter.native="updatePassword"
                v-model="passwordForm.confirmPassword"
                show-password
            />
          </el-form-item>
          <el-button
              type="primary"
              style="margin-left:9.4rem;margin-top: 1rem"
              @click="updatePassword"
          >
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
import axios from "axios";
import {ElNotification} from "element-plus";
import store from "../../store";

export default {
  name: "Setting",
  data() {
    return {
      infoForm: {
        avatar: "",
        nickname: "",
        intro: "",
        website: ""
      },
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      },
      activeName: "info"
    }
  },
  methods: {
    listSetting() {
      axios.get("/api/user/myself").then(res => {
        this.infoForm = res.data.data
      })
    },
    updateAvatar(response) {
      if (this.infoForm.avatar !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.infoForm.avatar
          }
        })
      }
      this.infoForm.avatar = response
      axios.put("/api/user/changeAvatar", {
        avatar: response
      }).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
          store.state.avatar = response
        }
      })
    },
    updateInfo() {
      axios.post("/api/user/updateInfo", this.infoForm).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        }
        this.listSetting()
      })
    },
    updatePassword() {
      if (this.passwordForm.oldPassword.trim() === "") {
        this.$message.error("旧密码不能为空");
        return false;
      }
      if (this.passwordForm.newPassword.trim() === "") {
        this.$message.error("新密码不能为空");
        return false;
      }
      if (this.passwordForm.newPassword.length < 6) {
        this.$message.error("新密码不能少于6位");
        return false;
      }
      if (this.passwordForm.newPassword.length > 18) {
        this.$message.error("新密码不能多于18位");
        return false;
      }
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        this.$message.error("两次密码输入不一致");
        return false;
      }
      axios.post("/api/user/updatePassword", this.passwordForm).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        } else if (res.data.code === 404) {
          ElNotification.error({
            title: "失败",
            message: res.data.message
          })
          return false
        }
        this.passwordForm.oldPassword = "";
        this.passwordForm.newPassword = "";
        this.passwordForm.confirmPassword = "";
      })
    },
  },
  created() {
    this.listSetting()
  }
}
</script>

<style scoped>
.form {
  margin-top: 3rem;
}

.avatar-container {
  text-align: center;
}

.el-icon-message-solid {
  color: #f56c6c;
  margin-right: 0.3rem;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 4px;
}

.info-container {
  display: flex;
  align-items: center;
  margin-left: 20%;
  margin-top: 5rem;
}
</style>