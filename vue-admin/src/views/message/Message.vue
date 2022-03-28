<template>
  <el-card class="main-card" style="display: flex">
    <div class="title">{{ this.$route.name }}</div>
    <div class="review-menu">
      <span>状态</span>
      <span
          @click="changeReview(null)"
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
          :disabled="messageIdList.length === 0"
          @click="deleteMessages"
      >
        批量删除
      </el-button>
      <el-button
          type="success"
          icon="SuccessFilled"
          :disabled="checkMessageIdList.length === 0"
          @click="checkMessage"
      >
        批量通过
      </el-button>
      <!-- 数据筛选 -->
      <el-input
          clearable
          v-model="keywords"
          prefix-icon="search"
          placeholder="请输入用户昵称"
          style="width:200px;margin-left: 715px"
          @clear="searchMessages"
          @keyup.enter.native="searchMessages"
      />
      <el-button
          type="primary"
          icon="search"
          style="margin-left:1rem"
          @click="searchMessages"
      >
        搜索
      </el-button>
    </div>
    <!-- 表格展示 -->
    <el-table
        style="margin-top: 1.25rem;"
        border
        v-loading="loading"
        :data="messageList"
        @selection-change="selectionChange"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55px"/>
      <el-table-column prop="avatar" label="头像" align="center" width="150px">
        <template #default="scope">
          <el-image :append-to-body='true' :src="scope.row.avatar" style="height: 40px;width: 40px"
                    :preview-src-list="[scope.row.avatar]"/>
        </template>
      </el-table-column>
      <el-table-column
          prop="nickname"
          label="留言人"
          align="center"
          width="150px"
      />
      <el-table-column prop="messageContent" label="留言内容" align="center"/>
      <el-table-column
          prop="ipAddress"
          label="ip地址"
          align="center"
          width="150px"
      />
      <el-table-column
          prop="ipSource"
          label="ip来源"
          align="center"
          width="170px"
      />
      <!-- 状态 -->
      <el-table-column prop="isReview" label="状态" width="90px" align="center">
        <template #default="scope">
          <el-tag size="large" v-if="scope.row.isReview === 1" type="warning">审核中</el-tag>
          <el-tag size="large" v-if="scope.row.isReview === 0" type="success">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="留言时间"
          width="140px"
          align="center"
      >
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" width="160px" align="center">
        <template #default="scope">
          <el-button
              v-if="scope.row.isReview === 1"
              type="success"
              slot="reference"
              @click="updateMessageReview(scope.row.id)"
          >
            通过
          </el-button>
          <el-popconfirm
              style="margin-left:10px"
              confirm-button-text="确定"
              cancel-button-text="取消"
              title="确定删除吗？"
              @confirm="deleteMessage(scope.row.id)"
          >
            <template #reference>
              <el-button type="danger">
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
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import axios from "axios";
import {ElMessageBox, ElNotification, ElPopconfirm} from 'element-plus'

export default {
  name: "LeaveAMessage",
  data() {
    return {
      loading: true,
      locale: zhCn,
      messageList: [],
      messageIdList: [],
      checkMessageIdList: [],
      keywords: null,
      total: 100,
      currentPage: 1,
      pageSize: 10,
      isReview: null
    }
  },
  created() {
    this.listMessage();
  },
  methods: {
    sizeChange(size) {
      this.pageSize = size
      this.listMessage()
    },
    currentChange(current) {
      this.currentPage = current
      this.listMessage()
    },
    changeReview(review) {
      this.isReview = review;
    },
    selectionChange(messageList) {
      this.messageIdList = [];
      this.checkMessageIdList = [];
      messageList.forEach(item => {
        this.messageIdList.push(item.id);
        if (item.isReview === 1) {
          this.checkMessageIdList.push(item.id)
        }
      });
    },
    listMessage() {
      this.loading = true
      axios.get("/api/message/listMessage", {
        params: {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          isReview: this.isReview,
          keywords: this.keywords
        }
      }).then(res => {
        this.messageList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    searchMessages() {
      this.currentPage = 1
      this.listMessage()
    },
    updateMessageReview(id) {
      ElMessageBox.confirm(
          '是否通过该留言?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'success',
          }
      ).then(() => {
        axios.put("/api/message/restoreMessage", {
          id: id
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
          this.listMessage()
        })
      })
    },
    checkMessage() {
      ElMessageBox.confirm(
          '是否审核通过选中留言?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'success',
          }
      ).then(() => {
        axios.put("/api/message/checkMessage", {
          checkMessage: this.checkMessageIdList
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
          this.listMessage()
        })
      })
    },
    deleteMessages() {
      ElMessageBox.confirm(
          '是否删除选中留言?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        this.deleteMessage(null)
      })
    },
    deleteMessage(id) {
      let deleteIdList = []
      if (id !== null) {
        deleteIdList = [id]
      } else {
        deleteIdList = this.messageIdList
      }
      axios.delete("/api/message/deleteMessage", {
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
        this.listMessage()
      })
    },
  },
  watch: {
    isReview() {
      this.current = 1;
      this.listMessage()
    }
  },
  components: {
    ElPopconfirm
  }
}
</script>

<style scoped>
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