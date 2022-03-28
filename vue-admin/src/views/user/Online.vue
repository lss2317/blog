<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{this.$route.name}}</div>
    <div class="operation-container">
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-input
            v-model="keywords"
            prefix-icon="search"
            placeholder="请输入用户昵称"
            style="width:200px"
            @keyup.enter.native="listOnlineUsers"
        />
        <el-button
            type="primary"
            icon="search"
            style="margin-left:1rem"
            @click="listOnlineUsers"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table border v-loading="loading" :data="userList" style="margin-top: 1.25rem">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="avatar" label="头像" align="center" width="100">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" width="40" height="40" />
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" align="center" />
      <el-table-column prop="ipAddress" label="ip地址" align="center" />
      <el-table-column
          prop="ipSource"
          label="登录地址"
          align="center"
          width="200"
      />
      <el-table-column
          prop="browser"
          label="浏览器"
          align="center"
          width="160"
      />
      <el-table-column prop="os" label="操作系统" align="center" />
      <el-table-column
          prop="lastLoginTime"
          label="登录时间"
          align="center"
          width="200"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px" />
          {{ scope.row.lastLoginTime | dateTime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-popconfirm
              title="确定下线吗？"
              style="margin-left:10px"
              @confirm="removeOnlineUser(scope.row)"
          >
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete" /> 下线
            </el-button>
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
import zhCn from "element-plus/lib/locale/lang/zh-cn";

export default {
  name: "Online",
  data(){
    return{
      locale: zhCn,
      keywords: null,
      currentPage: 1,
      pageSize: 10,
      total: 200
    }
  },
  methods:{
    sizeChange(size){
      this.pageSize = size
    },
    currentChange(current){
      this.currentPage = current
    }
  }
}
</script>

<style scoped>

</style>