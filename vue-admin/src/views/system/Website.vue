<template>
  <el-card class="main-card">
    <el-tabs v-model="activeName">
      <!-- 修改信息 -->
      <el-tab-pane label="网站信息" name="info">
        <el-form
            label-width="100px"
            :model="websiteConfigForm"
            label-position="left"
        >
          <el-form-item label="网站头像">
            <el-upload
                class="avatar-uploader"
                action="/api/files/upload"
                :show-file-list="false"
                :on-change="beforeUpload1"
                :on-success="handleWebsiteAvatarSuccess"
            >
              <el-image
                  v-if="websiteConfigForm.websiteAvatar"
                  :src="websiteConfigForm.websiteAvatar"
                  class="avatar"
              />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus/>
              </el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="网站名称">
            <el-input
                v-model="websiteConfigForm.websiteName"
                style="width:400px"
            />
          </el-form-item>
          <el-form-item label="网站作者">
            <el-input
                v-model="websiteConfigForm.websiteAuthor"
                style="width:400px"
            />
          </el-form-item>
          <el-form-item label="网站简介">
            <el-input
                v-model="websiteConfigForm.websiteIntro"
                style="width:400px"
            />
          </el-form-item>
          <el-form-item label="网站创建日期">
            <el-config-provider :locale="locale">
              <el-date-picker
                  style="width:400px"
                  v-model="websiteConfigForm.websiteCreateTime"
                  type="date"
                  placeholder="选择日期"
              />
            </el-config-provider>
          </el-form-item>
          <el-form-item label="网站公告">
            <el-input
                v-model="websiteConfigForm.websiteNotice"
                placeholder="请输入公告内容"
                style="width:400px"
                type="textarea"
                :rows="5"
            />
          </el-form-item>
          <el-form-item label="备案号">
            <el-input
                v-model="websiteConfigForm.websiteRecordNo"
                style="width:400px"
            />
          </el-form-item>
          <el-form-item label="第三方登录">
            <el-checkbox-group v-model="websiteConfigForm.socialLoginList">
              <el-checkbox label="qq">QQ</el-checkbox>
              <el-checkbox label="weibo">微博</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-button
              type="primary"
              style="margin-left:6.3rem"
              @click="updateWebsiteConfig"
          >
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
      <!-- 网站公告 -->
      <el-tab-pane label="社交信息" name="notice">
        <el-form label-width="70px" :model="websiteConfigForm">
          <el-checkbox-group v-model="websiteConfigForm.socialUrlList">
            <el-form-item label="QQ">
              <el-input
                  v-model="websiteConfigForm.qq"
                  style="width:400px;margin-right:1rem"
              />
              <el-checkbox label="qq">是否展示</el-checkbox>
            </el-form-item>
            <el-form-item label="Github">
              <el-input
                  v-model="websiteConfigForm.github"
                  style="width:400px;margin-right:1rem"
              />
              <el-checkbox label="github">是否展示</el-checkbox>
            </el-form-item>
            <el-form-item label="Gitee">
              <el-input
                  v-model="websiteConfigForm.gitee"
                  style="width:400px;margin-right:1rem"
              />
              <el-checkbox label="gitee">是否展示</el-checkbox>
            </el-form-item>
            <el-button
                type="primary"
                style="margin-left:4.375rem"
                @click="updateWebsiteConfig"
            >
              修改
            </el-button>
          </el-checkbox-group>
        </el-form>
      </el-tab-pane>
      <!-- 修改密码 -->
      <el-tab-pane label="其他设置" name="password">
        <el-form
            label-width="120px"
            :model="websiteConfigForm"
            label-position="left"
        >
          <el-row style="width:600px">
            <el-col :md="12">
              <el-form-item label="用户头像">
                <el-upload
                    class="avatar-uploader"
                    action="/api/files/upload"
                    :show-file-list="false"
                    :before-upload="beforeUpload2"
                    :on-success="handleUserAvatarSuccess"
                >
                  <el-image
                      v-if="websiteConfigForm.userAvatar"
                      :src="websiteConfigForm.userAvatar"
                      class="avatar"
                  />
                  <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                  </el-icon>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :md="12">
              <el-form-item label="游客头像">
                <el-upload
                    class="avatar-uploader"
                    action="/api/files/upload"
                    :show-file-list="false"
                    :on-change="beforeUpload3"
                    :on-success="handleTouristAvatarSuccess"
                >
                  <el-image
                      v-if="websiteConfigForm.touristAvatar"
                      :src="websiteConfigForm.touristAvatar"
                      class="avatar"
                  />
                  <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                  </el-icon>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="邮箱通知">
            <el-radio-group v-model="websiteConfigForm.isEmailNotice">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="评论审核">
            <el-radio-group v-model="websiteConfigForm.isCommentReview">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="留言审核">
            <el-radio-group v-model="websiteConfigForm.isMessageReview">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="打赏状态">
            <el-radio-group v-model="websiteConfigForm.isReward">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-row style="width:600px" v-show="websiteConfigForm.isReward == 1">
            <el-col :md="12">
              <el-form-item label="微信收款码">
                <el-upload
                    class="avatar-uploader"
                    action="/api/files/upload"
                    :show-file-list="false"
                    :before-upload="beforeUpload4"
                    :on-success="handleWeiXinSuccess"
                >
                  <img
                      v-if="websiteConfigForm.weiXinQRCode"
                      :src="websiteConfigForm.weiXinQRCode"
                      class="avatar"
                  />
                  <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                  </el-icon>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :md="12">
              <el-form-item label="支付宝收款码">
                <el-upload
                    class="avatar-uploader"
                    action="/api/files/upload"
                    :show-file-list="false"
                    :before-upload="beforeUpload5"
                    :on-success="handleAlipaySuccess"
                >
                  <img
                      v-if="websiteConfigForm.alipayQRCode"
                      :src="websiteConfigForm.alipayQRCode"
                      class="avatar"
                  />
                  <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                  </el-icon>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="聊天室状态">
            <el-radio-group v-model="websiteConfigForm.isChatRoom">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
              label="Websocket地址"
              v-show="websiteConfigForm.isChatRoom === 1"
          >
            <el-input
                v-model="websiteConfigForm.websocketUrl"
                style="width:400px"
            />
          </el-form-item>
          <el-form-item label="音乐播放器状态">
            <el-radio-group v-model="websiteConfigForm.isMusicPlayer">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-button
              type="primary"
              style="margin-left:6.3rem"
              @click="updateWebsiteConfig"
          >
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
//引入element-plus中文包
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import axios from "axios";
import {ElNotification} from "element-plus";

export default {
  name: "Website",
  data() {
    return {
      locale: zhCn,
      websiteConfigForm: {
        websiteAvatar: "",
        websiteName: "",
        websiteAuthor: "",
        websiteIntro: "",
        websiteNotice: "",
        websiteCreateTime: null,
        websiteRecordNo: "",
        socialLoginList: [],
        socialUrlList: [],
        qq: "",
        github: "",
        gitee: "",
        userAvatar: "",
        touristAvatar: "",
        isReward: 1,
        weiXinQRCode: "",
        alipayQRCode: "",
        isChatRoom: 1,
        websocketUrl: "",
        isMusicPlayer: 1,
        isEmailNotice: 1,
        isCommentReview: 0,
        isMessageReview: 0
      },
      activeName: "info",
      tempAvatar: ""
    }
  },
  created() {
    this.getWebsiteConfig()
  },
  methods: {
    updateWebsiteConfig() {
      axios.post("/api/blogInfo/updateWebsiteConfig", this.websiteConfigForm).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        } else if (res.data.code === 400) {
          ElNotification.error({
            title: "失败",
            message: res.data.message
          })
        }
      })
    },
    getWebsiteConfig() {
      axios.get("/api/blogInfo/getWebsiteConfig").then(res => {
        this.websiteConfigForm = res.data
      })
    },
    beforeUpload1() {
      this.tempAvatar = this.websiteConfigForm.websiteAvatar
    },
    beforeUpload2() {
      this.tempAvatar = this.websiteConfigForm.userAvatar
    },
    beforeUpload3() {
      this.tempAvatar = this.websiteConfigForm.touristAvatar
    },
    beforeUpload4() {
      this.tempAvatar = this.websiteConfigForm.weiXinQRCode
    },
    beforeUpload5() {
      this.tempAvatar = this.websiteConfigForm.alipayQRCode
    },
    handleWebsiteAvatarSuccess(response) {
      this.websiteConfigForm.websiteAvatar = response
      if (this.tempAvatar !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.tempAvatar
          }
        }).then(() => {
          this.tempAvatar = ""
        })
      }
    },
    handleUserAvatarSuccess(response) {
      this.websiteConfigForm.userAvatar = response;
      if (this.tempAvatar !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.tempAvatar
          }
        }).then(() => {
          this.tempAvatar = ""
        })
      }
    },
    handleTouristAvatarSuccess(response) {
      this.websiteConfigForm.touristAvatar = response;
      if (this.tempAvatar !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.tempAvatar
          }
        }).then(() => {
          this.tempAvatar = ""
        })
      }
    },
    handleWeiXinSuccess(response) {
      this.websiteConfigForm.weiXinQRCode = response;
      if (this.tempAvatar !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.tempAvatar
          }
        }).then(() => {
          this.tempAvatar = ""
        })
      }
    },
    handleAlipaySuccess(response) {
      this.websiteConfigForm.alipayQRCode = response;
      if (this.tempAvatar !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.tempAvatar
          }
        }).then(() => {
          this.tempAvatar = ""
        })
      }
    },
  }
}
</script>

<style scoped>
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


</style>