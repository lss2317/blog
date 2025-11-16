<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 文章标题 -->
    <div class="article-title-container">
      <el-input
          v-model="article.articleTitle"
          placeholder="输入文章标题"
      />
      <el-button
          type="danger"
          class="save-btn"
          @click="saveArticleDraft"
          v-if="article.id == null || article.status === 3"
      >
        保存草稿
      </el-button>
      <el-button
          type="danger"
          @click="openModel"
          style="margin-left:10px"
      >
        发布文章
      </el-button>
    </div>
    <!-- 文章内容 -->
    <md-editor
        v-model="article.articleContent"
        placeholder="开始编辑..."
        @onUploadImg="onUploadImg"
        style="display: flex"/>

    <!-- 添加文章对话框 -->
    <el-dialog v-model="addOrEdit" width="40%" top="3vh" title="发布文章">
      <!-- 文章数据 -->
      <el-form ref="article" label-width="80px" :model="article">
        <!-- 文章分类 -->
        <el-form-item prop="classificationName" label="文章分类">
          <el-tag
              type="success"
              v-show="article.classificationName"
              style="margin:0 1rem 0 0"
              :closable="true"
              size="large"
              @close="removeCategory"
          >
            {{ article.classificationName }}
          </el-tag>
          <!--   分类选项-->
          <el-popover
              placement="bottom-start"
              width="460px"
              trigger="click"
              v-if="!article.classificationName"
          >
            <div class="popover-title">分类</div>
            <!-- 搜索框 -->
            <el-autocomplete
                style="width:100%"
                v-model="classificationName"
                :fetch-suggestions="searchCategories"
                placeholder="请输入分类名搜索"
                :trigger-on-focus="false"
                @select="handleSelectClassification"
            >
              <template #default="{ item }">
                <div class="select">{{ item.classificationName }}</div>
              </template>
            </el-autocomplete>
            <!-- 分类 -->
            <div class="popover-container">
              <div
                  v-for="item of classificationList"
                  :key="item.id"
                  class="category-item"
                  @click="addClassification(item)"
              >
                {{ item.classificationName }}
              </div>
            </div>
            <template #reference>
              <el-button type="success" plain slot="reference">
                添加分类
              </el-button>
            </template>
          </el-popover>

        </el-form-item>
        <!-- 文章标签 -->
        <el-form-item prop="tagNames" label="文章标签">
          <el-tag
              v-for="(item, index) of article.tagNames"
              :key="index"
              size="large"
              style="margin:0 1rem 0 0"
              :closable="true"
              @close="removeTag(item)"
          >
            {{ item }}
          </el-tag>
          <!-- 标签选项 -->
          <el-popover
              placement="bottom-start"
              width="460px"
              trigger="click"
              v-if="article.tagNames.length < 3"
          >
            <div class="popover-title">标签</div>
            <!-- 搜索框 -->
            <el-autocomplete
                style="width:100%"
                v-model="tagName"
                :fetch-suggestions="searchTags"
                placeholder="请输入标签名搜索"
                :trigger-on-focus="false"
                @select="handleSelectTag"
            >
              <template #default="{ item }">
                <div class="select">{{ item.tagName }}</div>
              </template>
            </el-autocomplete>
            <!-- 标签 -->
            <div class="popover-container">
              <div style="margin-bottom:1rem">添加标签</div>
              <el-tag
                  v-for="(item, index) of tagList"
                  :key="index"
                  style="margin-right: 1.25rem;margin-top: 1rem"
                  size="large"
                  :class="this.tagClass(item)"
                  @click="addTag(item)"
              >
                {{ item.tagName }}
              </el-tag>
            </div>
            <template #reference>
              <el-button type="primary" plain slot="reference">
                添加标签
              </el-button>
            </template>
          </el-popover>
        </el-form-item>
        <el-form-item prop="type" label="文章类型">
          <el-select v-model="article.type" placeholder="请选择类型">
            <el-option
                v-for="item in typeList"
                :key="item.type"
                :label="item.desc"
                :value="item.type"
            />
          </el-select>
        </el-form-item>
        <!-- 文章类型 -->
        <el-form-item prop="originalUrl" label="原文地址" v-if="article.type !== 1">
          <el-input
              v-model="article.originalUrl"
              placeholder="请填写原文链接"
          />
        </el-form-item>
        <el-form-item prop="articleCover" label="上传封面">
          <el-upload
              class="upload-demo"
              drag
              ref="upload"
              :limit=this.limit
              action="/api/files/upload"
              :on-success="uploadCover"
              :before-upload="beforeUpload"
              :on-remove="removeUpload"
          >
            <el-icon class="el-icon--upload" v-if="article.articleCover === ''">
              <upload-filled/>
            </el-icon>
            <div class="el-upload__text" v-if="article.articleCover === ''">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
                v-else
                :src="article.articleCover"
                style="width: 360px;height: 180px"
            />
          </el-upload>
        </el-form-item>
        <el-form-item prop="isTop" label="置顶">
          <el-switch
              v-model="article.isTop"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              :active-value="1"
              :inactive-value="0"
          />
        </el-form-item>
        <el-form-item prop="status" label="发布形式">
          <el-radio-group v-model="article.status">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="2">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="danger" @click="saveOrUpdateArticle">
          发 表
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import MdEditor from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import axios from "axios";
import {ElNotification} from "element-plus";
import * as imageConversion from "image-conversion";
import config from "../../assets/js/config";
import store from '../../store'

export default {
  name: "PostArticle",
  components: {
    MdEditor
  },
  data() {
    return {
      addOrEdit: false,
      classificationName: "",
      tagName: "",
      classificationList: [],
      tagList: [],
      limit: 1,
      typeList: [
        {
          type: 1,
          desc: "原创"
        },
        {
          type: 2,
          desc: "转载"
        },
        {
          type: 3,
          desc: "翻译"
        }
      ],
      article: {
        id: null,
        articleTitle: this.dateTypeFormat('YYYY-mm-dd HH:MM:SS', new Date()),
        articleContent: "",
        articleCover: "",
        classificationName: null,
        tagNames: [],
        originalUrl: "",
        isTop: 0,
        type: 1,
        status: 1
      }
    }
  },
  created() {
    if (this.$route.query.articleId) {
      axios.get("/api/article/admin/" + this.$route.query.articleId).then(res => {
        this.article = res.data.data
      })
    } else {
      if (store.state.draft === true) {
        this.article = store.state.article
      }
    }
  },
  methods: {
    async onUploadImg(files, callback) {
      const res = await Promise.all(
          Array.from(files).map((file) => {
            return new Promise((rev, rej) => {
              const form = new FormData();
              if (file.size / 1024 < config.UPLOAD_SIZE) {
                form.append('file', file);
                axios.post('/api/files/upload', form, {
                  headers: {
                    'Content-Type': 'multipart/form-data'
                  }
                }).then((res) => rev(res))
                    .catch((error) => rej(error));
              } else {
                imageConversion.compressAccurately(file, config.UPLOAD_SIZE).then(res => {
                  form.append("file", new window.File([res], file.name, {type: file.type}));
                  axios.post('/api/files/upload', form, {
                    headers: {
                      'Content-Type': 'multipart/form-data'
                    }
                  }).then((res) => rev(res))
                      .catch((error) => rej(error));
                })
              }
            });
          })
      );
      callback(res.map(item => item.data));
    },
    dateTypeFormat(fmt, date) {
      let ret
      const opt = {
        'Y+': date.getFullYear().toString(), // 年
        'm+': (date.getMonth() + 1).toString(), // 月
        'd+': date.getDate().toString(), // 日
        'H+': date.getHours().toString(), // 时
        'M+': date.getMinutes().toString(), // 分
        'S+': date.getSeconds().toString() // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
      }
      for (const k in opt) {
        ret = new RegExp('(' + k + ')').exec(fmt)
        if (ret) {
          fmt = fmt.replace(ret[1], (ret[1].length === 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, '0')))
        }
      }
      return fmt
    },
    saveOrUpdateArticle() {
      if (this.article.classificationName === null) {
        this.$message.error("文章分类不能为空");
        return false;
      }
      if (this.article.tagNames.length === 0) {
        this.$message.error("文章标签不能为空");
        return false;
      }
      if (this.article.articleCover.trim() === "") {
        this.$message.error("文章封面不能为空");
        return false;
      }
      axios.post("/api/article/saveArticle", this.article).then(res => {
        this.article.articleTitle = this.dateTypeFormat('YYYY-mm-dd HH:MM:SS', new Date())
        this.article.articleContent = ""
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
          if (this.$refs.article !== undefined) {
            this.$refs.article.resetFields()
          }
          if (this.$refs.upload !== undefined) {
            this.$refs.upload.clearFiles()
          }
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
        }
        if (this.article.id === null) {
          store.commit("dropDraft")
        }
        this.addOrEdit = false
      })
    },
    beforeUpload(file) {
      return new Promise(resolve => {
        if (file.size / 1024 < config.UPLOAD_SIZE) {
          resolve(file);
        }
        // 压缩到200KB,这里的200就是要压缩的大小,可自定义
        imageConversion
            .compressAccurately(file, config.UPLOAD_SIZE)
            .then(res => {
              resolve(res);
            });
      });
    },
    listTags() {
      axios.get("/api/tag/search", {
        params: {
          keywords: null
        }
      }).then(res => {
        this.tagList = res.data.data
      })
    },
    listClassification() {
      axios.get("/api/classification/search", {
        params: {
          keywords: null
        }
      }).then(res => {
        this.classificationList = res.data.data
      })
    },
    removeCategory() {
      this.article.classificationName = null;
    },
    openModel() {
      if (this.article.articleTitle.trim() === "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() === "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      this.listTags()
      this.listClassification()
      this.addOrEdit = true;
    },
    handleSelectTag(item) {
      this.addTag({
        tagName: item.tagName
      });
    },
    addClassification(item) {
      this.article.classificationName = item.classificationName
    },
    addTag(item) {
      if (this.article.tagNames.indexOf(item.tagName) === -1) {
        this.article.tagNames.push(item.tagName);
      }
    },
    handleSelectClassification(item) {
      this.addClassification({
        classificationName: item.classificationName
      });
    },
    removeUpload() {
      axios.get("/api/files/delete", {
        params: {
          key: this.article.articleCover
        }
      }).then(() => {
        this.article.articleCover = ""
      })
    },
    searchCategories(keywords, cb) {
      axios.get("/api/classification/search", {
        params: {
          keywords: keywords
        }
      }).then(res => {
        cb(res.data.data)
      })
    },
    searchTags(keywords, cb) {
      axios.get("/api/tag/search", {
        params: {
          keywords: keywords
        }
      }).then(res => {
        cb(res.data.data)
      })
    },
    removeTag(item) {
      const index = this.article.tagNames.indexOf(item);
      this.article.tagNames.splice(index, 1);
    },
    uploadCover(response) {
      this.article.articleCover = response
    },
    saveArticleDraft() {
      if (this.article.articleTitle.trim() === "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() === "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      store.commit('saveDraft', this.article)
      ElNotification.success({
        title: "成功",
        message: "保存草稿成功"
      })
    }
  },
  computed: {
    tagClass() {
      return function (item) {
        const index = this.article.tagNames.indexOf(item.tagName);
        return index !== -1 ? "tag-item-select" : "tag-item";
      };
    }
  }
}
</script>

<style scoped>
.article-title-container {
  display: flex;
  align-items: center;
  margin-bottom: 1.25rem;
  margin-top: 2.25rem;
}

.save-btn {
  margin-left: 0.75rem;
  background: #fff;
  color: #f56c6c;
}

.select:hover {
  background-color: #f0f9eb;
  color: #67c23a;
}

.category-item {
  cursor: pointer;
  padding: 0.6rem 0.5rem;
}

.category-item:hover {
  background-color: #f0f9eb;
  color: #67c23a;
}

.tag-item {
  margin-right: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
}

.tag-item-select {
  margin-right: 1rem;
  margin-bottom: 1rem;
  cursor: not-allowed;
  color: #ccccd8 !important;
}

.popover-title {
  margin-bottom: 1rem;
  text-align: center;
}

.popover-container {
  margin-top: 1rem;
  height: 260px;
  overflow-y: auto;
}
</style>