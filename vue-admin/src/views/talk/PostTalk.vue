<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <div class="talk-container">
      <!-- 输入框 -->
      <Editor
          ref="editor"
          class="editor-wrapper"
          v-model="talk.content"
          placeholder="说点什么吧"
      />
      <!-- 操作菜单 -->
      <div class="operation-wrapper">
        <div class="left-wrapper">
          <!-- 表情 -->
          <el-popover placement="bottom-start" width="268px" trigger="click">
            <span
                class="emoji-item"
                v-for="(value, key, index) of emojiList"
                :key="index"
                @click="addEmoji(key, value)"
            >
              <img
                  :src="value"
                  :title="key"
                  class="emoji"
                  width="24"
                  height="24"
              />
            </span>
            <template #reference>
              <i class="iconfont operation-btn">&#xe627;</i>
            </template>
          </el-popover>
          <!-- 图片上传 -->
          <el-upload
              action="/api/files/upload"
              multiple
              :on-success="upload"
              :show-file-list="false"
          >
            <i class="iconfont operation-btn">&#xe607;</i>
          </el-upload>
        </div>
        <div class="right-wrapper">
          <!-- 是否置顶 -->
          <el-switch
              style="margin-right:16px"
              v-model="talk.isTop"
              inactive-text="置顶"
              :active-value="1"
              :inactive-value="0"
          />
          <!-- 说说状态 -->
          <el-dropdown
              trigger="click"
              @command="handleCommand"
              style="margin-right:16px"
          >
            <span class="talk-status">
              {{ dropdownTitle }}<el-icon><arrow-down/></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                    v-for="(item, index) of statusList"
                    :key="index"
                    :command="item.status"
                >
                  {{ item.desc }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button
              type="primary"
              @click="saveOrUpdateTalk"
              :disabled="talk.content === ''"
          >
            发布
          </el-button>
        </div>
      </div>
      <!-- 图片上传 -->
      <el-upload
          class="talk-image-upload"
          v-show="uploadList.length > 0"
          action="/api/files/upload"
          list-type="picture-card"
          :file-list="uploadList"
          multiple
          :on-success="upload"
          :on-remove="handleRemove"
      >
        <el-icon>
          <Plus/>
        </el-icon>
      </el-upload>
    </div>
  </el-card>
</template>

<script>
import EmojiList from "../../assets/js/emoji";
import Editor from "../../components/Editor.vue";
import axios from "axios";
import {ElNotification} from "element-plus";

export default {
  name: "PostTalk",
  data() {
    return {
      emojiList: EmojiList,
      talk: {
        id: null,
        content: "",
        isTop: 0,
        status: 1,
        images: null
      },
      statusList: [
        {status: 1, desc: "公开"},
        {status: 2, desc: "私密"}
      ],
      uploadList: []
    }
  },
  methods: {
    handleCommand(command) {
      this.talk.status = command;
    },
    addEmoji(key, value) {
      this.$refs.editor.addText(
          "<img src= '" +
          value +
          "' width='24'height='24' alt=" +
          key +
          " style='margin: 0 1px;vertical-align: text-bottom'/>"
      );
    },
    upload(response) {
      this.uploadList.push({url: response});
    },
    handleRemove(file) {
      axios.get("/api/files/delete", {
        params: {
          key: file.url
        }
      })
      this.uploadList.forEach((item, index) => {
        if (item.url === file.url) {
          this.uploadList.splice(index, 1);
        }
      });
    },
    saveOrUpdateTalk() {
      console.log(this.uploadList)
      if (this.talk.content.trim() === "") {
        this.$message.error("说说内容不能为空");
        return false;
      }
      // 转换图片
      if (this.uploadList.length > 0) {
        let imgList = [];
        this.uploadList.forEach(item => {
          imgList.push(item.url);
        });
        this.talk.images = JSON.stringify(imgList);
      } else {
        this.talk.images = null
      }
      axios.post("/api/talk/saveOrUpdateTalk", this.talk).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        } else if (res.data.code === 500) {
          ElNotification.warning({
            title: "错误",
            message: res.data.message
          })
        } else if (res.data.code === 400) {
          ElNotification.error({
            title: "失败",
            message: res.data.message
          })
          return false
        }
        this.uploadList = []
        this.talk = {
          id: null,
          content: "",
          isTop: 0,
          status: 1,
          images: null
        }
        this.$refs.editor.clear();
      })
    },
  },
  created() {
    if (this.$route.params.talkId) {
      axios.get("/api/talk/" + this.$route.params.talkId).then(res => {
        this.talk = res.data.data;
        this.$refs.editor.addText(res.data.data.content)
        if (res.data.data.imgList) {
          res.data.data.imgList.forEach(item => {
            this.uploadList.push({url: item});
          });
        }
      });
    }
  },
  components: {
    Editor
  },
  computed: {
    dropdownTitle() {
      let desc = "";
      this.statusList.forEach(item => {
        if (item.status === this.talk.status) {
          desc = item.desc;
        }
      });
      return desc;
    }
  }
}
</script>

<style scoped>
.talk-container {
  margin-top: 40px;
}

.editor-wrapper {
  min-height: 150px;
}

.operation-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.operation-btn {
  cursor: pointer;
  color: #838383;
  font-size: 20px;
  margin-right: 12px;
}

.talk-status {
  cursor: pointer;
  font-size: 12px;
  color: #999;
  line-height: 32px;
}

.emoji {
  user-select: none;
  margin: 0.4rem;
  display: inline-block;
  vertical-align: middle;
}

.emoji-item {
  cursor: pointer;
  display: inline-block;
}

.emoji-item:hover {
  transition: all 0.2s;
  border-radius: 0.25rem;
  background: #dddddd;
}

.left-wrapper {
  display: flex;
  width: 50%;
}

.talk-image-upload {
  margin-top: 8px;
}
</style>