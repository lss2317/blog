<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <el-button
          type="primary"
          icon="plus"
          @click="openModel(null)"
      >
        新建页面
      </el-button>
    </div>
    <!-- 空状态 -->
    <el-empty v-if="pageList.length === 0" description="暂无页面"/>
    <!-- 相册列表 -->
    <el-row class="page-container" :gutter="12" v-loading="loading">
      <el-col v-for="item of pageList" :key="item.id" :md="6">
        <div class="page-item">
          <!-- 相册操作 -->
          <div class="page-operation">
            <el-dropdown @command="handleCommand">
              <el-icon style="color:#fff">
                <more-filled />
              </el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="'update' + JSON.stringify(item)">
                    <el-icon>
                      <edit-pen/>
                    </el-icon>
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item :command="'delete' + item.id">
                    <el-icon>
                      <delete/>
                    </el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <el-image fit="cover" class="page-cover" :preview-src-list="[item.pageCover]" :src="item.pageCover"/>
          <div class="page-name">{{ item.pageName }}</div>
        </div>
      </el-col>
    </el-row>
    <!-- 新增模态框 -->
    <el-dialog v-model="addOrEdit" width="35%" top="10vh" :title="pageTitle">
      <el-form label-width="80px" size="medium" :model="pageForum">
        <el-form-item label="页面名称">
          <el-input style="width:220px" v-model="pageForum.pageName"/>
        </el-form-item>
        <el-form-item label="页面标签">
          <el-input style="width:220px" v-model="pageForum.pageLabel"/>
        </el-form-item>
        <el-form-item label="页面封面">
          <el-upload
              class="upload-cover"
              drag
              :show-file-list="false"
              action="/api/files/upload"
              multiple
              :before-upload="beforeLoad"
              :on-success="uploadCover"
          >
            <el-icon class="el-icon--upload" v-if="pageForum.pageCover === ''">
              <upload-filled/>
            </el-icon>
            <div class="el-upload__text" v-if="pageForum.pageCover === ''">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
                v-else
                :src="pageForum.pageCover"
                style="height: 180px;width: 360px"
            />
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditPage">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import axios from "axios";
import {ElMessageBox, ElNotification} from "element-plus";

export default {
  name: "page",
  data() {
    return {
      loading: true,
      addOrEdit: false,
      pageTitle: null,
      pageForum: {
        id: null,
        pageName: "",
        pageLabel: "",
        pageCover: ""
      },
      pageList: [],
      tempCover: "",
    }
  },
  methods: {
    openModel(item) {
      if (item) {
        this.pageForum = JSON.parse(item);
        this.pageTitle = "修改页面";
      } else {
        this.pageForum = {
          id: null,
          pageName: "",
          pageLabel: "",
          pageCover: ""
        };
        this.pageTitle = "新建页面";
      }
      this.addOrEdit = true;
    },
    listPages() {
      this.loading = true
      axios.get("/api/page/listPages").then(res => {
        this.pageList = res.data.data
        this.loading = false
      })
    },
    beforeLoad() {
      this.tempCover = this.pageForum.pageCover
    },
    uploadCover(response) {
      this.pageForum.pageCover = response
      if (this.tempCover !== "") {
        axios.get("/api/files/delete", {
          params: {
            key: this.tempCover
          }
        }).then(() => {
          this.tempCover = ""
        })
      }
    },
    addOrEditPage() {
      if (this.pageForum.pageName.trim() === "") {
        this.$message.error("页面名称不能为空");
        return false;
      }
      if (this.pageForum.pageLabel.trim() === "") {
        this.$message.error("页面标签不能为空");
        return false;
      }
      if (this.pageForum.pageCover === "") {
        this.$message.error("页面封面不能为空");
        return false;
      }
      axios.post("/api/page/saveOrUpdatePage", this.pageForum).then(res => {
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
        this.listPages()
      })
    },
    handleCommand(command) {
      const type = command.substring(0, 6);
      const data = command.substring(6);
      if (type === "delete") {
        this.deleteOpen(data)
      } else {
        this.openModel(data);
      }
    },
    deleteOpen(id) {
      ElMessageBox.confirm(
          '确定要删除该页面吗?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        axios.post("/api/page/deletePage", {
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
            this.addOrEdit = false
            return false
          }
          this.listPages()
        })
      }).catch(() => {
      })
    }
  },
  created() {
    this.listPages()
  }
}
</script>

<style scoped>
.page-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}

.page-name {
  text-align: center;
  margin-top: 0.5rem;
}

.page-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.page-operation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}

.page-container {
  margin-top: 1.25rem;
}

.operation-container{
  margin-top: 2.25rem;
}
</style>