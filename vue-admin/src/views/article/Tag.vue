<template>
  <el-card class="main-card" style="display: flex">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container" style="display: flex">
      <el-button
          type="primary"
          icon="Plus"
          @click="openModel(null)"
      >
        新增
      </el-button>
      <el-button
          :disabled="this.tagIdList.length === 0"
          type="danger"
          icon="Delete"
          @click="deleteTags"
      >
        批量删除
      </el-button>

      <el-input
          clearable
          v-model="keywords"
          prefix-icon="Search"
          placeholder="请输入标签名"
          style="width:200px;margin-left: 752px"
          @keyup.enter="searchTags"
          @clear="listTags(null)"
      />
      <el-button
          type="primary"
          style="margin-left:1rem"
          icon="Search"
          @click="searchTags"
      >
        搜索
      </el-button>
    </div>
    <!-- 表格展示 -->
    <el-table
        border
        v-loading="loading"
        :data="tagList"
        @selection-change="selectionChange"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <!-- 标签名 -->
      <el-table-column prop="tagName" label="标签名" align="center">
        <template #default="scope">
          <el-tag size="large" style="margin-left: 5px">
            {{ scope.row.tagName }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章量 -->
      <el-table-column prop="articleCount" label="文章量" align="center">
        <template #default="scope">
          {{ scope.row.articleTotal }}
        </template>
      </el-table-column>
      <!-- 标签创建时间 -->
      <el-table-column prop="createTime" label="创建时间" align="center">
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" icon="edit" @click="openModel(scope.row)">
            编辑
          </el-button>
          <el-popconfirm
              title="确定删除吗？"
              style="margin-left:1rem"
              @confirm="deleteTag(scope.row.id)"
              confirm-button-text="删除"
              cancel-button-text="取消"
              :ref="`popover-${scope.$index}`"
          >
            <template #reference>
              <el-button type="danger" size="small" slot="reference" icon="Delete">
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
          layout="total, sizes, prev, pager,next"
      />
    </el-config-provider>
    <!-- 编辑对话框 -->
    <el-dialog v-model="addOrEdit" width="30%" :title="title">
      <el-form label-width="80px" size="medium" :model="tagForm">
        <el-form-item label="标签名" style="margin-left: 9%">
          <el-input style="width:220px" v-model="tagForm.tagName"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditTag">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
//引入element-plus中文包
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import axios from "axios";
import {ElMessageBox, ElNotification, ElPopconfirm} from 'element-plus'

export default {
  name: "Tag",
  data() {
    return {
      locale: zhCn,
      tempTagName: null,
      keywords: null,
      tagIdList: [],
      total: 100,
      pageSize: 10,
      currentPage: 1,
      title: null,
      tagForm: {
        id: null,
        tagName: "",
      },
      addOrEdit: false,
      tagList: [],
      loading: true,
    }
  },
  methods: {
    sizeChange(size) {
      //每页显示条数改变
      this.pageSize = size;
      this.listTags(this.keywords)
    },
    currentChange(current) {
      this.currentPage = current
      this.listTags(this.keywords)
    },
    searchTags() {
      this.currentPage = 1
      this.listTags(this.keywords)
    },
    selectionChange(tagList) {
      this.tagIdList = []
      tagList.forEach(item => {
        this.tagIdList.push(item.id)
      })
    },
    openModel(tag) {
      if (tag != null) {
        this.tagForm = JSON.parse(JSON.stringify(tag));
        this.title = "修改标签";
        this.tempTagName = this.tagForm.tagName
      } else {
        this.tagForm.id = null;
        this.tagForm.tagName = "";
        this.title = "添加标签";
      }
      this.addOrEdit = true;
    },
    addOrEditTag() {
      if (this.tagForm.tagName.trim() === "") {
        this.$message.warning("标签名不能为空");
        return false;
      }
      if (this.title === "修改标签") {
        if (this.tempTagName === this.tagForm.tagName) {
          this.$message.warning("请修改标签")
          return false;
        }
        axios.post("/api/tag/modifyTag", this.tagForm).then(res => {
          if (res.data.code === 100) {
            ElNotification.error({
              title: "失败",
              message: res.data.message
            })
          } else if (res.data.code === 200) {
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
          this.listTags(null)
        })
      }
      if (this.title === "添加标签") {
        axios.post("/api/tag/addTag", this.tagForm).then(res => {
          if (res.data.code === 100) {
            ElNotification.error({
              title: "失败",
              message: res.data.message
            })
          } else if (res.data.code === 200) {
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
          this.listTags(null)
        })
      }
      this.addOrEdit = false
    },
    listTags(keywords) {
      this.loading = true
      axios.get("/api/tag/listTag", {
        params: {
          keywords: keywords,
          pageSize: this.pageSize,
          currentPage: this.currentPage
        }
      }).then(res => {
        this.tagList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    deleteTag(id) {
      axios.get("/api/tag/deleteTag", {
        params: {
          id: id
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
        this.listTags(this.keywords)
      })
    },
    deleteTags() {
      ElMessageBox.confirm(
          '是否删除选中项?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        axios.delete("/api/tag/deleteTags", {
              data: this.tagIdList
            }
        ).then(res => {
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
          this.listTags(this.keywords)
        })
      }).catch(() => {
      })
    }
  },
  created() {
    this.listTags(null)
  },
  components: {
    ElPopconfirm
  }
}
</script>

<style scoped>
.operation-container {
  display: flex;
  align-items: center;
  margin-bottom: 1.25rem;
  margin-top: 2.25rem;
}

.pagination-container {
  float: right;
  margin-top: 1.25rem;
  margin-bottom: 1.25rem;
}
</style>