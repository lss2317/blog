<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
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
          :disabled="linkIdList.length === 0"
          @click="deleteLinks"
      >
        批量删除
      </el-button>
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <el-input
            clearable
            v-model="keywords"
            prefix-icon="search"
            placeholder="请输入友链名"
            style="width:200px"
            @clear="searchLinks"
            @keyup.enter.native="listFriendLink"
        />
        <el-button
            type="primary"
            icon="search"
            style="margin-left:1rem"
            @click="searchLinks"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        border
        :data="linkList"
        style="margin-top: 1.25rem"
        @selection-change="selectionChange"
        v-loading="loading"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column
          prop="linkAvatar"
          label="链接头像"
          align="center"
          width="180px"
      >
        <template #default="scope">
          <el-image
              :src="scope.row.linkAvatar"
              style="width: 40px;height: 40px"
              :append-to-body='true'
              :preview-src-list="[scope.row.linkAvatar]"
          />
        </template>
      </el-table-column>
      <el-table-column prop="linkName" label="链接名" align="center"/>
      <el-table-column prop="linkAddress" label="链接地址" align="center"/>
      <el-table-column prop="linkIntro" label="链接介绍" align="center"/>
      <el-table-column
          prop="createTime"
          label="创建时间"
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
      <el-table-column label="操作" align="center" width="160px">
        <template #default="scope">
          <el-button type="primary" @click="openModel(scope.row)">
            编辑
          </el-button>
          <el-popconfirm
              title="确定删除吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              style="margin-left:1rem"
              @confirm="deleteLink(scope.row.id)"
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
          class="pagination-container"
          background
          @size-change="sizeChange"
          @current-change="currentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="count"
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next"
      />
    </el-config-provider>
    <!-- 添加对话框 -->
    <el-dialog v-model="addOrEdit" width="30%" :title="linkTitle">
      <el-form label-width="80px" size="medium" :model="linkForm">
        <el-form-item label="链接名">
          <el-input style="width:250px" v-model="linkForm.linkName"/>
        </el-form-item>
        <el-form-item label="链接头像">
          <el-input style="width:250px" v-model="linkForm.linkAvatar"/>
        </el-form-item>
        <el-form-item label="链接地址">
          <el-input style="width:250px" v-model="linkForm.linkAddress"/>
        </el-form-item>
        <el-form-item label="链接介绍">
          <el-input style="width:250px" v-model="linkForm.linkIntro"/>
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
import axios from "axios";
import {ElMessageBox, ElNotification, ElPopconfirm} from "element-plus";
//引入element-plus中文包
import zhCn from 'element-plus/lib/locale/lang/zh-cn'

export default {
  name: "FriendLink",
  data() {
    return {
      locale: zhCn,
      loading: true,
      linkIdList: [],
      linkList: [],
      addOrEdit: false,
      linkTitle: null,
      linkForm: {
        id: null,
        linkName: "",
        linkAvatar: "",
        linkIntro: "",
        linkAddress: ""
      },
      keywords: null,
      currentPage: 1,
      pageSize: 10,
      count: 100,
    }
  },
  methods: {
    sizeChange(size) {
      this.pageSize = size
      this.listFriendLink()
    },
    currentChange(current) {
      this.currentPage = current
      this.listFriendLink()
    },
    selectionChange(linkList) {
      this.linkIdList = [];
      linkList.forEach(item => {
        this.linkIdList.push(item.id);
      });
    },
    deleteLink(id) {
      axios.post("/api/link/deleteLink", {
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
        this.listFriendLink()
      })
    },
    openModel(link) {
      if (link != null) {
        this.linkForm = JSON.parse(JSON.stringify(link));
        this.linkTitle = "修改友链";
      } else {
        this.linkForm.id = null;
        this.linkForm.linkName = "";
        this.linkForm.linkAvatar = "";
        this.linkForm.linkIntro = "";
        this.linkForm.linkAddress = "";
        this.linkTitle = "添加友链";
      }
      this.addOrEdit = true;
    },
    addOrEditCategory() {
      if (this.linkForm.linkName.trim() === "") {
        this.$message.error("友链名不能为空");
        return false;
      }
      if (this.linkForm.linkAvatar.trim() === "") {
        this.$message.error("友链头像不能为空");
        return false;
      }
      if (this.linkForm.linkAddress.trim() === "") {
        this.$message.error("友链地址不能为空");
        return false;
      }
      if (this.linkForm.linkIntro.trim() === "") {
        this.$message.error("友链介绍不能为空");
        return false;
      }
      axios.post("/api/link/saveOrUpdateLink", this.linkForm).then(res => {
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
          this.addOrEdit = false
          return false
        }
        this.addOrEdit = false
        this.listFriendLink()
      })
    },
    listFriendLink() {
      this.loading = true
      axios.get("/api/link/listFriendLink", {
        params: {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          keywords: this.keywords
        }
      }).then(res => {
        this.linkList = res.data.data.data
        this.count = res.data.data.count
        this.loading = false
      })
    },
    searchLinks() {
      this.currentPage = 1
      this.listFriendLink()
    },
    deleteLinks() {
      ElMessageBox.confirm(
          '确定要删除选择友链?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        axios.delete("/api/link/deleteLinks", {
          data: this.linkIdList
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
          this.listFriendLink()
        })
      })
    }
  },
  created() {
    this.listFriendLink()
  },
  components: {
    ElPopconfirm
  }
}
</script>

<style scoped>

</style>