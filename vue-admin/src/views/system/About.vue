<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <md-editor
        v-model="aboutContent"
        placeholder="开始编辑..."
        style="height:calc(100vh - 250px);margin-top:2.25rem"
    />
    <el-button
        type="danger"
        class="edit-btn"
        @click="updateAbout"
    >
      修改
    </el-button>
  </el-card>
</template>

<script>
import MdEditor from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import axios from "axios";
import {ElNotification} from "element-plus";

export default {
  name: "About",
  data() {
    return {
      aboutContent: ""
    }
  },
  methods: {
    getAbout() {
      axios.get("/api/blogInfo/getAbout").then(res => {
        this.aboutContent = res.data.updateAbout
      })
    },
    updateAbout() {
      axios.post("/api/blogInfo/updateAbout", {
        updateAbout: this.aboutContent
      }).then(res => {
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
          return false
        }
        this.getAbout()
      })
    }
  },
  components: {
    MdEditor
  },
  created() {
    this.getAbout()
  }
}
</script>

<style scoped>
.edit-btn {
  float: right;
  margin: 1rem 0;
}
</style>