<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <div class="review-menu">
      <span>状态</span>
      <span
          @click="this.changeReview(null)"
          :class="isReview === null ? 'active-review' : 'review'"
      >
        全部
      </span>
      <span
          @click="changeReview(0)"
          :class="isReview === 0 ? 'active-review' : 'review'"
      >
        正常
      </span>
      <span
          @click="changeReview(1)"
          :class="isReview === 1 ? 'active-review' : 'review'"
      >
        审核中
      </span>
    </div>
    <!-- 表格操作 -->
    <div class="operation-container" style="display: flex">
      <el-button
          type="danger"
          icon="delete"
          :disabled="commentIdList.length === 0"
          @click="deleteComments(null)"
      >
        批量删除
      </el-button>
      <el-button
          type="success"
          icon="successFilled"
          :disabled="isReviewCommentIdList.length === 0"
          @click="updateCommentReview(null)"
      >
        批量通过
      </el-button>
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-select
            clearable
            v-model="type"
            placeholder="请选择来源"
            style="margin-right:1rem;"
        >
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <el-input
            clearable
            v-model="keywords"
            prefix-icon="search"
            placeholder="请输入用户昵称"
            style="width:200px"
            @keyup.enter.native="searchComments"
            @clear="searchComments"
        />
        <el-button
            type="primary"
            icon="search"
            style="margin-left:1rem"
            @click="searchComments"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        border
        :data="commentList"
        @selection-change="selectionChange"
        v-loading="loading"
        style="margin-top: 1.23rem"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="avatar" label="头像" align="center" width="120">
        <template #default="scope">
          <el-image :src="scope.row.avatar" :append-to-body='true' :preview-src-list="[scope.row.avatar]"
                    style="width: 40px;height: 40px"/>
        </template>
      </el-table-column>
      <!-- 评论人昵称 -->
      <el-table-column
          prop="nickname"
          label="评论人"
          align="center"
          width="120"
      />
      <!-- 回复人昵称 -->
      <el-table-column
          prop="replyNickname"
          label="回复人"
          align="center"
          width="120"
      >
        <template #default="scope">
          <span v-if="scope.row.replyNickname">
            {{ scope.row.replyNickname }}
          </span>
          <el-tag v-else size="large">无</el-tag>
        </template>
      </el-table-column>
      <!-- 评论文章标题 -->
      <el-table-column prop="articleTitle" label="文章标题" align="center">
        <template #default="scope">
          <span v-if="scope.row.articleTitle">
            {{ scope.row.articleTitle }}
          </span>
          <el-tag v-else size="large">无</el-tag>
        </template>
      </el-table-column>
      <!-- 评论内容 -->
      <el-table-column prop="commentContent" label="评论内容" align="center">
        <template #default="scope">
          <span v-html="scope.row.commentContent" class="comment-content"/>
        </template>
      </el-table-column>
      <!-- 评论时间 -->
      <el-table-column
          prop="createTime"
          label="评论时间"
          width="150"
          align="center"
      >
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <!-- 状态 -->
      <el-table-column prop="isReview" label="状态" width="90" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.isReview === 1" size="large" type="warning">审核中</el-tag>
          <el-tag v-if="scope.row.isReview === 0" size="large" type="success">正常</el-tag>
        </template>
      </el-table-column>
      <!-- 来源 -->
      <el-table-column label="来源" align="center" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 1" size="large">文章</el-tag>
          <el-tag v-if="scope.row.type === 2" size="large" type="warning">友链</el-tag>
          <el-tag v-if="scope.row.type === 3" size="large" type="danger">说说</el-tag>
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" width="160" align="center">
        <template #default="scope">
          <el-button
              v-if="scope.row.isReview === 1"
              type="success"
              slot="reference"
              @click="updateCommentReview(scope.row.id)"
          >
            通过
          </el-button>
          <el-button type="danger" @click="deleteComments(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-config-provider :locale="locale">
      <el-pagination
          class="pagination-container"
          background
          @size-change="sizeChange"
          @current-change="currentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next"
      />
    </el-config-provider>
  </el-card>
</template>

<script>
import zhCn from "element-plus/lib/locale/lang/zh-cn";
import {ElMessageBox, ElNotification, ElPopconfirm} from 'element-plus'
import axios from "axios";

export default {
  name: "Comment",
  data() {
    return {
      locale: zhCn,
      commentList: [],
      commentIdList: [],
      isReviewCommentIdList: [],
      keywords: null,
      pageSize: 10,
      currentPage: 1,
      total: 100,
      loading: true,
      type: null,
      isReview: null,
      options: [
        {
          value: 1,
          label: "文章"
        },
        {
          value: 2,
          label: "友链"
        },
        {
          value: 3,
          label: "说说"
        }
      ],
    }
  },
  methods: {
    sizeChange(size) {
      this.pageSize = size
      this.listComments()
    },
    currentChange(current) {
      this.currentPage = current
      this.listComments()
    },
    changeReview(review) {
      this.current = 1;
      this.isReview = review;
    },
    selectionChange(commentList) {
      this.commentIdList = [];
      this.isReviewCommentIdList = []
      commentList.forEach(item => {
        this.commentIdList.push(item.id);
        if (item.isReview === 1) {
          this.isReviewCommentIdList.push(item.id)
        }
      });
    },
    deleteComments(id) {
      ElMessageBox.confirm(
          '是否删除评论?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error',
          }
      ).then(() => {
        let deleteIdList = []
        if (id !== null) {
          deleteIdList = [id]
        } else {
          deleteIdList = this.commentIdList
        }
        axios.delete("/api/comment/deleteComments", {
          data: deleteIdList
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
            return false
          }
          this.listComments()
        })
      }).catch(() => {
      })
    },
    searchComments() {
      this.currentPage = 1
      this.listComments()
    },
    listComments() {
      this.loading = true
      axios.get("/api/comment/listAdminComment", {
        params: {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          type: this.type,
          isReview: this.isReview,
          keywords: this.keywords
        }
      }).then(res => {
        this.commentList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    updateCommentReview(id) {
      ElMessageBox.confirm(
          '是否通过评论?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'success',
          }
      ).then(() => {
        let isReviewList = []
        if (id !== null) {
          isReviewList = [id]
        } else {
          isReviewList = this.isReviewCommentIdList
        }
        axios.put("/api/comment/checkComments", {
          isReviewList: isReviewList
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
            return false
          }
          this.listComments()
        })
      }).catch(() => {
      })
    }
  },
  created() {
    this.listComments()
  },
  watch: {
    isReview() {
      this.current = 1;
      this.listComments();
    },
    type() {
      this.current = 1;
      this.listComments();
    }
  },
  components: {
    ElPopconfirm
  }
}
</script>

<style scoped>
.comment-content {
  display: inline-block;
}

.operation-container {
  margin-top: 1.5rem;
}

.review-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}

.review-menu span {
  margin-right: 24px;
}

.review {
  cursor: pointer;
}

.active-review {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}
</style>
