<template>
  <el-card class="main-card" style="display: flex">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 文章状态 -->
    <div class="article-status-menu">
      <span>状态</span>
      <span @click="changeStatus('all')" :class="this.isActive('all')">全部</span>
      <span @click="changeStatus('public')" :class="isActive('public')">
        公开
      </span>
      <span @click="changeStatus('secret')" :class="isActive('secret')">
        私密
      </span>
      <span @click="changeStatus('delete')" :class="isActive('delete')">
        回收站
      </span>
    </div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button
          type="danger"
          :disabled="articleIdList.length === 0"
          @click="deleteArticles"
      >
        <el-icon>
          <Delete/>
        </el-icon>
        &nbsp;&nbsp;批量删除
      </el-button>
      <!-- 文章类型 -->
      <el-select
          clearable
          v-model="articleType"
          placeholder="请选择文章类型"
          style="margin-right:1rem;margin-left: 136px"
      >
        <el-option
            v-for="item in typeList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        />
      </el-select>
      <!-- 分类 -->
      <el-select
          clearable
          v-model="classificationName"
          filterable
          placeholder="请选择分类"
          style="margin-right:1rem"
          @clear="listArticle"
          @change="listArticle"
      >
        <el-option
            v-for="item in classificationList"
            :key="item.id"
            :label="item.classificationName"
            :value="item.classificationName"
        />
      </el-select>
      <!-- 标签 -->
      <el-select
          clearable
          v-model="tagName"
          filterable
          placeholder="请选择标签"
          style="margin-right:1rem"
      >
        <el-option
            v-for="item in tagList"
            :key="item.id"
            :label="item.tagName"
            :value="item.tagName"
        />
      </el-select>
      <!-- 文章名 -->
      <el-input
          clearable
          v-model="keywords"
          prefix-icon="Search"
          placeholder="请输入文章名"
          style="width:200px"
          @keyup.enter="searchArticles"
          @clear="listArticle"
      />
      <el-button
          type="primary"
          style="margin-left:1rem"
          @click="searchArticles"

      >
        <el-icon>
          <Search/>
        </el-icon>
        &nbsp;&nbsp;搜索
      </el-button>
    </div>

    <!-- 表格展示 -->
    <el-table
        border
        :data="articleList"
        @selection-change="selectionChange"
        v-loading="loading"
        style="margin-top: 13px"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55px"/>
      <!-- 文章修改时间 -->
      <el-table-column
          label="文章封面"
          width="180px"
          align="center"
      >
        <template #default="scope">
          <el-image
              class="article-cover"
              :append-to-body='true'
              :preview-src-list="[scope.row.articleCover]"
              :src="scope.row.articleCover"
          ></el-image>
        </template>
      </el-table-column>
      <!-- 文章标题 -->
      <el-table-column prop="articleTitle" label="标题" align="center"/>
      <!-- 文章分类 -->
      <el-table-column
          prop="classificationName"
          label="分类"
          width="110px"
          align="center"
      />
      <!-- 文章标签 -->
      <el-table-column
          label="标签"
          width="170px"
          align="center"
      >
        <template #default="scope">
          <el-tag
              size="large"
              v-for="item of scope.row.tagNameList"
              :key="item.id"
              style="margin-right:0.2rem;margin-top:0.2rem"
          >
            {{ item.tagName }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章浏览量 -->
      <el-table-column
          prop="viewsCount"
          label="浏览量"
          width="70px"
          align="center"
      >
        <template #default="scope">
          <span v-if="scope.row.viewsCount">
            {{ scope.row.viewsCount }}
          </span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <!-- 文章点赞量 -->
      <el-table-column
          prop="likeCount"
          label="点赞量"
          width="70px"
          align="center"
      >
        <template #default="scope">
          <span v-if="scope.row.likeCount">
            {{ scope.row.likeCount }}
          </span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <!-- 文章类型 -->
      <el-table-column prop="type" label="类型" width="80" align="center">
        <template #default="scope">
          <el-tag size="large" :type="type[scope.row.type].type">
            {{ this.type[scope.row.type].desc }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章发表时间 -->
      <el-table-column
          prop="createTime"
          label="发表时间"
          width="130px"
          align="center"
      >
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <!-- 文章置顶 -->
      <el-table-column prop="isTop" label="置顶" width="80px" align="center">
        <template #default="scope">
          <el-switch
              v-model="scope.row.isTop"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              :disabled="scope.row.isDelete === 1"
              :active-value="1"
              :inactive-value="0"
              @change="changeTop(scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="160px">
        <template #default="scope">
          <el-button
              type="primary"
              @click="editArticle(scope.row)"
              v-if="scope.row.isDelete === 0"
          >
            编辑
          </el-button>
          <el-popconfirm
              title="确定删除吗？"
              style="margin-left:10px"
              confirm-button-text="确定"
              cancel-button-text="取消"
              v-if="scope.row.isDelete === 0"
              @confirm="deleteArticle(scope.row)"
          >
            <template #reference>
              <el-button type="danger" slot="reference">
                删除
              </el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm
              title="确定恢复吗？"
              v-if="scope.row.isDelete === 1"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="restoreArticle(scope.row)"
          >
            <template #reference>
              <el-button type="success" slot="reference">
                恢复
              </el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm
              style="margin-left:10px"
              v-if="scope.row.isDelete === 1"
              confirm-button-text="确定"
              cancel-button-text="取消"
              title="确定彻底删除吗？"
              @confirm="deleteArticle(scope.row)"
          >
            <template #reference>
              <el-button type="danger" slot="reference">
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-config-provider :locale="locale">
      <el-pagination
          locale="zhCn"
          class="pagination-container"
          background
          @size-change="sizeChange"
          @current-change="currentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[5,10]"
          layout="total, sizes, prev, pager, next"
      />
    </el-config-provider>
  </el-card>
</template>

<script>
import {Search, Delete} from '@element-plus/icons-vue'
//引入element-plus中文包
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import axios from "axios";
import {ElMessageBox, ElNotification, ElPopconfirm} from 'element-plus'

export default {
  name: "ArticleList",
  data() {
    return {
      articleIdList: [],
      typeList: [
        {
          value: 1,
          label: "原创"
        },
        {
          value: 2,
          label: "转载"
        },
        {
          value: 3,
          label: "翻译"
        }
      ],
      classificationList: [],
      tagList: [],
      articleType: null,
      classificationName: null,
      tagName: null,
      keywords: null,
      background: true,
      total: 100,
      currentPage: 1,
      pageSize: 5,
      locale: zhCn,
      loading: true,
      articleList: [],
      deleteStatus: 0,
      status: 0,
      activeStatus: 'all',
      type: [
        {
          type: '',
          desc: ''
        },
        {
          type: '',
          desc: "原创"
        },
        {
          type: 'danger',
          desc: "转载"
        },
        {
          type: 'success',
          desc: "翻译"
        }
      ],
    }
  },
  components: {
    Search,
    Delete,
    ElPopconfirm
  },
  methods: {
    sizeChange(size) {
      //每页显示条数改变
      this.pageSize = size;
      this.listArticle()
    },
    currentChange(current) {
      //改变页码
      this.currentPage = current;
      this.listArticle()
    },
    selectionChange(articleList) {
      this.articleIdList = []
      articleList.forEach(item => {
        this.articleIdList.push(item.id)
      })
    },
    listArticle() {
      this.loading = true
      axios.get("/api/article/listArticle", {
        params: {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          articleType: this.articleType,
          classificationName: this.classificationName,
          tagName: this.tagName,
          keywords: this.keywords,
          deleteStatus: this.deleteStatus,
          status: this.status
        }
      }).then(res => {
        this.articleList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    listClassifications() {
      axios.get("/api/classification").then(res => {
        this.classificationList = res.data.data
      })
    },
    listTags() {
      axios.get("/api/tag").then(res => {
        this.tagList = res.data.data
      })
    },
    searchArticles() {
      this.loading = true
      this.currentPage = 1
      this.listArticle()
    },
    changeStatus(status) {
      switch (status) {
        case "all":
          this.deleteStatus = 0;
          this.status = 0;
          break;
        case "public":
          this.deleteStatus = 0;
          this.status = 1
          break;
        case "secret":
          this.deleteStatus = 0;
          this.status = 2
          break;
        case "delete":
          this.deleteStatus = 1;
          this.status = 0
          break;
      }
      this.activeStatus = status
      this.listArticle()
    },
    editArticle(article) {
      this.$router.push({
        name: "发布文章", params: {
          articleId: article.id
        }
      })
    },
    deleteArticle(item) {
      axios.get("/api/article/deleteArticle", {
        params: {
          id: item.id,
          deleteStatus: item.isDelete
        }
      }).then(res => {
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
          return
        }
        this.listArticle()
      })
    },
    restoreArticle(item) {
      axios.get("/api/article/restoreArticle", {
        params: {
          id: item.id
        }
      }).then(res => {
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
          return
        }
        this.listArticle()
      })
    },
    deleteArticles() {
      ElMessageBox.confirm(
          '是否删除选中项?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        axios.delete("/api/article/deleteArticles", {
          data: this.articleIdList
        }).then(res => {
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
            this.remove = false
            this.isDelete = false
            return
          }
          this.remove = false
          this.isDelete = false
          this.listArticle()
        })
      }).catch(() => {
      })
    },
    changeTop(article) {
      axios.get("/api/article/changeTop", {
        params: {
          id: article.id,
          isTop: article.isTop
        }
      }).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        } else if (res.data.code === 500) {
          ElNotification.warning({
            title: "警告",
            message: res.data.message
          })
        } else if (res.data.code === 400) {
          ElNotification.error({
            title: "失败",
            message: res.data.message
          })
        }
      })
    }
  },
  created() {
    this.listArticle()
    this.listClassifications()
    this.listTags()
  },
  computed: {
    isActive() {
      return function (status) {
        return this.activeStatus === status ? "active-status" : "status";
      };
    }
  },
  watch: {
    articleType() {
      this.currentPage = 1
      this.listArticle()
    },
    classificationName() {
      this.currentPage = 1
      this.listArticle()
    },
    tagName() {
      this.currentPage = 1
      this.listArticle()
    }
  }
}
</script>

<style scoped>
.operation-container {
  margin-top: 1.5rem;
  display: flex;
}

.article-status-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}

.article-status-menu span {
  margin-right: 24px;
}

.article-cover {
  position: relative;
  width: 100%;
  height: 90px;
  border-radius: 4px;
}

.status {
  cursor: pointer;
}

.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}

.pagination-container {
  float: right;
  margin-top: 1.25rem;
  margin-bottom: 1.25rem;
}

.demo-pagination-block + .demo-pagination-block {
  margin-top: 10px;
}

.demo-pagination-block .demonstration {
  margin-bottom: 16px;
}
</style>