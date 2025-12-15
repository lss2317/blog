<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <!-- 登录方式 -->
        <el-select
            clearable
            v-model="loginType"
            placeholder="请选择登录方式"
            style="margin-right:1rem"
        >
          <el-option
              v-for="item in typeList"
              :key="item.type"
              :label="item.desc"
              :value="item.type"
          />
        </el-select>
        <el-input
            clearable
            v-model="keywords"
            prefix-icon="search"
            placeholder="请输入昵称"
            style="width:200px"
            @keyup.enter.native="searchUsers"
            @clear="listUser"
        />
        <el-button
            type="primary"
            icon="search"
            style="margin-left:1rem"
            @click="searchUsers"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table border :data="userList" v-loading="loading" style="margin-top: 1.25rem;">
      <!-- 表格列 -->
      <el-table-column
          prop="linkAvatar"
          label="头像"
          align="center"
      >
        <template #default="scope">
          <el-image :src="scope.row.avatar"
                    :preview-src-list="[scope.row.avatar]"
                    :append-to-body='true'
                    style="width: 40px;height: 40px"/>
        </template>
      </el-table-column>
      <el-table-column
          prop="nickname"
          label="用户昵称"
          align="center"
      />
      <el-table-column
          prop="loginType"
          label="登录方式"
          align="center"
      >
        <template #default="scope">
          <el-tag type="success" size="large" v-if="scope.row.loginType === 1">邮箱</el-tag>
          <el-tag size="large" v-if="scope.row.loginType === 2">QQ</el-tag>
          <el-tag size="large" type="danger" v-if="scope.row.loginType === 3">微博</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleList" label="用户角色" align="center">
        <template #default="scope">
          <el-tag type="success" size="large" v-if="scope.row.role === 0">普通用户</el-tag>
          <el-tag size="large" type="warning" v-if="scope.row.role === 1">测试账号</el-tag>
          <el-tag size="large" type="danger" v-if="scope.row.role === 2">管理员</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isDisable" label="禁用" align="center" width="100px">
        <template #default="scope">
          <el-switch
              v-model="scope.row.isDisable"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              :active-value="1"
              :inactive-value="0"
              @change="changeDisable(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
          prop="ipAddress"
          label="登录ip"
          align="center"
          width="140px"
      />
      <el-table-column
          prop="ipSource"
          label="登录地址"
          align="center"
          width="140px"
      />
      <el-table-column
          prop="createTime"
          width="130px"
          label="创建时间"
          align="center"
      >
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <el-table-column
          prop="lastLoginTime"
          label="上次登录时间"
          width="130px"
          align="center"
      >
        <template #default="scope">
          <el-icon style="margin-right: 5px">
            <timer/>
          </el-icon>
          {{ scope.row.lastLoginTime }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="110px">
        <template #default="scope">
          <el-button
              type="primary"
              icon="edit"
              @click="openEditModel(scope.row)"
          >
            编辑
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
    <!-- 修改对话框 -->
    <el-dialog v-model="isEdit" width="30%" title=" 修改用户">
      <el-form label-width="60px" size="medium" :model="userForm">
        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname" style="width:220px"/>
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="userForm.role">
            <el-radio :label="2">管理员</el-radio>
            <el-radio :label="0">普通用户</el-radio>
            <el-radio :label="1">测试账号</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="isEdit = false">取 消</el-button>
        <el-button type="primary" @click="editUserRole">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import zhCn from "element-plus/lib/locale/lang/zh-cn";
import axios from "axios";
import {ElNotification} from 'element-plus'

export default {
  name: "User",
  data() {
    return {
      locale: zhCn,
      loginType: null,
      keywords: null,
      total: 99,
      pageSize: 10,
      currentPage: 1,
      userForm: {
        id: null,
        userInfoId: null,
        nickname: "",
        role: null
      },
      typeList: [
        {
          type: 1,
          desc: "邮箱"
        },
        {
          type: 2,
          desc: "QQ"
        },
        {
          type: 3,
          desc: "微博"
        }
      ],
      userList: null,
      loading: true,
      isEdit: false,
    }
  },
  methods: {
    sizeChange(size) {
      this.pageSize = size
      this.listUser()
    },
    currentChange(current) {
      this.currentPage = current
      this.listUser()
    },
    listUser() {
      this.loading = true
      axios.get("/api/user/listUser", {
        params: {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          loginType: this.loginType,
          keywords: this.keywords
        }
      }).then(res => {
        this.userList = res.data.data.data
        this.total = res.data.data.count
        this.loading = false
      })
    },
    openEditModel(user) {
      this.roleIdList = [];
      this.userForm = JSON.parse(JSON.stringify(user));
      this.isEdit = true;
    },
    searchUsers() {
      this.listUser()
    },
    editUserRole() {
      axios.post("/api/user/updateRole", this.userForm).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        } else if (res.data.code === 500) {
          ElNotification.info({
            title: "警告",
            message: res.data.message
          })
        } else if (res.data.code === 400) {
          ElNotification.error({
            title: "失败",
            message: res.data.message
          })
          this.isEdit = false
          return
        }
        this.listUser()
        this.isEdit = false
      })
    },
    changeDisable(user) {
      axios.get("/api/user/changeDisable", {
        params: {
          id: user.id,
          isDisable: user.isDisable
        }
      }).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
        } else if (res.data.code === 500) {
          ElNotification.info({
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
    this.listUser()
  },
  watch: {
    loginType() {
      this.listUser()
    }
  }
}
</script>

<style scoped>

</style>