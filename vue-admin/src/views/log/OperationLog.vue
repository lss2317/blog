<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container" style=" margin-top: 2.25rem;">
      <el-button
          type="danger"
          icon="delete"
          :disabled="this.logIdList.length === 0"
          @click="deleteLogs"
      >
        批量删除
      </el-button>
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-input
            clearable
            v-model="keywords"
            prefix-icon="search"
            placeholder="请输入模块名或描述"
            style="width:200px"
            @clear="listLogs"
            @keyup.enter.native="searchLogs"
        />
        <el-button
            type="primary"
            icon="search"
            style="margin-left:1rem"
            @click="searchLogs"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table
        style="margin-top: 1.25rem;"
        @selection-change="selectionChange"
        v-loading="loading"
        :data="logList"
    >
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column
          prop="optModule"
          label="系统模块"
          align="center"
          width="120px"
      />
      <el-table-column
          width="100px"
          prop="optType"
          label="操作类型"
          align="center"
      />
      <el-table-column
          prop="optDesc"
          label="操作描述"
          align="center"
          width="150px"
      />
      <el-table-column
          prop="requestMethod"
          label="请求方式"
          align="center"
          width="100px"
      >
        <template #default="scope">
          <el-tag size="large" :type="tagType(scope.row.requestMethod)">
            {{ scope.row.requestMethod }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="操作人员" align="center"/>
      <el-table-column
          prop="ipAddress"
          label="登录ip"
          align="center"
          width="130px"
      />
      <el-table-column
          prop="ipSource"
          label="登录地址"
          align="center"
          width="150px"
      />
      <el-table-column
          prop="createTime"
          label="操作日期"
          align="center"
          width="190px"
      >
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150px">
        <template #default="scope">
          <el-button
              type="text"
              slot="reference"
              style="font-size: 13px"
              @click="check(scope.row)"
          >
            <el-icon style="margin-right: 2px">
              <View/>
            </el-icon>
            查看
          </el-button>
          <el-button @click="deleteLog(scope.row.id)" type="text" style="font-size: 13px">
            <el-icon style="margin-right: 2px">
              <delete/>
            </el-icon>
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
    <!-- 查看模态框 -->
    <el-dialog v-model="isCheck" width="40%" title="详细信息">
      <el-form ref="form" :model="optLog" label-width="100px" size="mini">
        <el-form-item label="操作模块：">
          {{ optLog.optModule }}
        </el-form-item>
        <el-form-item label="请求地址：">
          {{ optLog.optUrl }}
        </el-form-item>
        <el-form-item label="请求方式：">
          <el-tag size="large" :type="tagType(optLog.requestMethod)">
            {{ optLog.requestMethod }}
          </el-tag>
        </el-form-item>
        <el-form-item label="操作方法：">
          {{ optLog.optMethod }}
        </el-form-item>
        <el-form-item label="请求参数：">
          {{ optLog.requestParam }}
        </el-form-item>
        <el-form-item label="返回数据：">
          {{ optLog.responseData }}
        </el-form-item>
        <el-form-item label="操作人员：">
          {{ optLog.nickname }}
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script>
import zhCn from "element-plus/lib/locale/lang/zh-cn";
import axios from "axios";
import {ElMessageBox, ElNotification} from 'element-plus'

export default {
  name: "OperationLog",
  data() {
    return {
      locale: zhCn,
      logList: [],
      logIdList: [],
      keywords: null,
      currentPage: 1,
      pageSize: 10,
      total: 100,
      loading: true,
      isCheck: false,
      optLog: {},
    }
  },
  methods: {
    sizeChange(size) {
      this.pageSize = size
      this.listLogs()
    },
    currentChange(current) {
      this.currentPage = current
      this.listLogs()
    },
    selectionChange(logList) {
      this.logIdList = [];
      logList.forEach(item => {
        this.logIdList.push(item.id);
      });
    },
    listLogs() {
      this.loading = true
      axios.get("/api/log/listLogs", {
        params: {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          keywords: this.keywords
        }
      }).then(res => {
        this.logList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    deleteLog(id) {
      ElMessageBox.confirm(
          '是否删除该日志?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error',
          }
      ).then(() => {
        axios.post("/api/log/deleteLog", {
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
          this.listLogs()
        })
      }).catch(() => {
      })
    },
    deleteLogs() {
      ElMessageBox.confirm(
          '是否删除选中项?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error',
          }
      ).then(() => {
            axios.delete("/api/log/deleteLogs", {
              data: this.logIdList
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
              this.listLogs()
            })
          }
      ).catch(() => {
      })
    },
    searchLogs() {
      this.currentPage = 1
      this.listLogs()
    }
    ,
    check(optLog) {
      this.optLog = JSON.parse(JSON.stringify(optLog));
      this.isCheck = true;
    }
  },
  created() {
    this.listLogs()
  }
  ,
  computed: {
    tagType() {
      return function (type) {
        switch (type) {
          case "GET":
            return "success";
          case "POST":
            return "";
          case "PUT":
            return "warning";
          case "DELETE":
            return "danger";
        }
      };
    }
  }
}
</script>

<style scoped>
label {
  font-weight: bold !important;
}

</style>