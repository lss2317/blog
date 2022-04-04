<template>
  <el-card class="main-card" style="display: flex">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container" style="display: flex">
      <el-button
          type="primary"
          icon="plus"
          @click="openModel(null)"
      >
        新增
      </el-button>
      <el-button
          type="danger"
          icon="delete"
          size="default"
          :disabled="this.classIdList.length === 0"
          @click="deleteClassifications"
      >
        批量删除
      </el-button>
      <el-input
          clearable
          v-model="keywords"
          prefix-icon="search"
          placeholder="请输入分类名"
          style="width:200px;margin-left: 740px"
          @keyup.enter="searchClassification"
          @clear="listClassification(null)"
      />
      <el-button
          type="primary"
          icon="search"
          style="margin-left:1rem"
          @click="searchClassification"
      >
        搜索
      </el-button>
    </div>
    <!-- 表格展示 -->
    <el-table
        border
        :data="classificationList"
        @selection-change="selectionChange"
        v-loading="loading"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <!-- 分类名 -->
      <el-table-column prop="classificationName" label="分类名" align="center"/>
      <!-- 文章量 -->
      <el-table-column prop="articleTotal" label="文章量" align="center"/>
      <!-- 分类创建时间 -->
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
          <el-button icon="edit" type="primary" size="small" @click="openModel(scope.row)">
            编辑
          </el-button>
          <el-button icon="delete" size="small" type="danger" @click="deleteCategory(scope.row.id)">
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
    <!-- 添加编辑对话框 -->
    <el-dialog v-model="addOrEdit" width="30%" :title="title">
      <el-form label-width="80px" size="medium" :model="classificationForm">
        <el-form-item label="分类名" style="margin-left: 9%">
          <el-input v-model="classificationForm.classificationName" style="width:220px"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditCategory">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
//引入element-plus中文包
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import {ElMessageBox, ElNotification} from 'element-plus'
import axios from "axios";

export default {
  name: "Classification",
  data() {
    return {
      locale: zhCn,
      classIdList: [],
      total: 100,
      pageSize: 10,
      currentPage: 1,
      keywords: null,
      tempClassificationName: null,
      loading: true,
      classificationForm: {
        id: null,
        classificationName: ""
      },
      addOrEdit: false,
      title: null,
      classificationList: [],
    }
  },
  methods: {
    sizeChange(size) {
      this.pageSize = size
      this.listClassification(this.keywords)
    },
    currentChange(current) {
      this.currentPage = current
      this.listClassification(this.keywords)
    },
    searchClassification() {
      this.currentPage = 1
      this.listClassification(this.keywords)
    },
    selectionChange(classIdList) {
      this.classIdList = []
      classIdList.forEach(item => {
        this.classIdList.push(item.id)
      })
    },
    openModel(classification) {
      if (classification != null) {
        this.title = "修改分类"
        this.classificationForm = JSON.parse(JSON.stringify(classification))
        this.tempClassificationName = this.classificationForm.classificationName
      } else {
        this.title = "添加分类"
        this.classificationForm.id = null;
        this.classificationForm.classificationName = "";
      }
      this.addOrEdit = true
    },
    addOrEditCategory() {
      if (this.classificationForm.classificationName.trim() === "") {
        this.$message.warning("分类名不能为空");
        return false;
      }
      if (this.title === "修改分类") {
        if (this.tempClassificationName === this.classificationForm.classificationName) {
          this.$message.warning("请修改分类名")
          return false;
        }
        axios.post("/api/classification/modifyClassification", this.classificationForm).then(res => {
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
          this.listClassification(this.keywords)
        })
      }
      if (this.title === "添加分类") {
        axios.post("/api/classification/addClassification", this.classificationForm).then(res => {
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
              this.listClassification(this.keywords)
            }
        )
      }
      this.addOrEdit = false
    },
    listClassification(keywords) {
      this.loading = true
      axios.get("/api/classification/listClassification", {
        params: {
          keywords: keywords,
          pageSize: this.pageSize,
          currentPage: this.currentPage
        }
      }).then(res => {
        this.classificationList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    deleteCategory(id) {
      ElMessageBox.confirm(
          '是否删除该分类?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error',
          }
      ).then(() => {
        axios.get("/api/classification/deleteClassification", {
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
          this.listClassification(this.keywords)
        })
      })
          .catch(() => {
          })
    },
    deleteClassifications() {
      ElMessageBox.confirm(
          '是否删除选中项?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error',
          }
      ).then(() => {
        axios.delete("/api/classification/deleteClassifications", {
              data: this.classIdList
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
          this.listClassification(this.keywords)
        })
      }).catch(() => {
      })
    }
  },
  created() {
    this.listClassification(null)
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