<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container" style=" margin-top: 2.25rem;">
      <el-button
          type="primary"
          icon="plus"
          @click="openModel(null)"
      >
        新建相册
      </el-button>
      <div style="margin-left:auto">
        <el-button
            type="text"
            icon="delete"
            style="margin-right:1rem"
            @click="checkDelete"
        >
          回收站
        </el-button>
        <el-input
            clearable
            v-model="keywords"
            prefix-icon="search"
            placeholder="请输入相册名"
            style="width:200px"
            @keyup.enter.native="searchAlbums"
            @clear="listAlbum"
        />
        <el-button
            type="primary"
            icon="search"
            style="margin-left:1rem"
            @click="searchAlbums"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 空状态 -->
    <el-empty v-if="albumList.length === 0" description="暂无相册"/>
    <!-- 相册列表 -->
    <el-row class="album-container" :gutter="12" v-loading="loading">
      <el-col v-for="item of albumList" :key="item.id" :md="6">
        <div class="album-item" @click="checkPhoto(item)">
          <!-- 相册操作 -->
          <div class="album-operation">
            <el-dropdown @command="handleCommand">
              <el-icon style="color:#fff;cursor: pointer;">
                <more-filled />
              </el-icon>
              <template #dropdown>
                <el-dropdown-menu slot="dropdown">
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
          <el-image fit="cover" class="album-cover" :src="item.albumCover"/>
          <div class="album-name">{{ item.albumName }}</div>
        </div>
      </el-col>
    </el-row>
    <!-- 分页 -->
    <el-pagination
        :hide-on-single-page="true"
        class="pagination-container"
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page="current"
        :page-size="size"
        :total="count"
        layout="prev, pager, next"
    />
    <!-- 新增模态框 -->
    <el-dialog v-model="addOrEdit" width="35%" top="10vh" :title="albumTitle">
      <el-form label-width="80px" size="medium" :model="photoAlbum">
        <el-form-item label="相册名称">
          <el-input style="width:220px" v-model="photoAlbum.albumName"/>
        </el-form-item>
        <el-form-item label="相册描述">
          <el-input style="width:220px" v-model="photoAlbum.albumDesc"/>
        </el-form-item>
        <el-form-item label="相册封面">
          <el-upload
              class="upload-cover"
              drag
              :show-file-list="false"
              action="/api/files/upload"
              multiple
              :before-upload="changeCover"
              :on-success="uploadCover"
          >
            <el-icon class="el-icon--upload" v-if="photoAlbum.albumCover === ''">
              <upload-filled/>
            </el-icon>
            <div class="el-upload__text" v-if="photoAlbum.albumCover === ''">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
                v-else
                :src="photoAlbum.albumCover"
                style="width: 360px;height: 180px"
            />
          </el-upload>
        </el-form-item>
        <el-form-item label="发布形式">
          <el-radio-group v-model="photoAlbum.status">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="2">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditAlbum">
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
  name: "AlbumList",
  data() {
    return {
      albumTitle: "",
      tempCover: null,
      keywords: "",
      loading: true,
      isDelete: false,
      addOrEdit: false,
      photoAlbum: {
        id: null,
        albumName: "",
        albumDesc: "",
        albumCover: "",
        status: 1
      },
      albumList: [],
      current: 1,
      size: 8,
      count: 0
    }
  },
  methods: {
    sizeChange(size) {
      this.size = size
      this.listAlbum()
    },
    currentChange(current) {
      this.current = current
      this.listAlbum()
    },
    checkDelete() {
      this.$router.push("/deletePhoto")
    },
    checkPhoto(item) {
      this.$router.push({path: "/albumList/" + item.id})
    },
    handleCommand(command) {
      const type = command.substring(0, 6);
      const data = command.substring(6);
      if (type === "delete") {
        this.open(data)
      } else {
        this.openModel(data);
      }
    },
    uploadCover(response) {
      this.photoAlbum.albumCover = response
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
    open(id) {
      ElMessageBox.confirm(
          '确定要删除该相册吗?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        axios.get("/api/album/deleteAlbum", {
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
          }
          this.listAlbum()
        })
      }).catch(() => {
      })
    },
    listAlbum() {
      this.loading = true
      axios.get("/api/album/listAlbum", {
        params: {
          currentPage: this.current,
          pageSize: this.size,
          keywords: this.keywords
        }
      }).then(res => {
        this.albumList = res.data.data.data
        this.count = res.data.data.count
        this.loading = false
      })
    },
    changeCover() {
      this.tempCover = this.photoAlbum.albumCover
    },
    openModel(item) {
      if (item) {
        this.photoAlbum = JSON.parse(item);
        this.albumTitle = "修改相册";
      } else {
        this.photoAlbum = {
          id: null,
          albumName: "",
          albumDesc: "",
          albumCover: "",
          status: 1
        };
        this.albumTitle = "新建相册";
      }
      this.addOrEdit = true;
    },
    addOrEditAlbum() {
      if (this.photoAlbum.albumName.trim() === "") {
        this.$message.error("相册名称不能为空");
        return false;
      }
      if (this.photoAlbum.albumDesc.trim() === "") {
        this.$message.error("相册描述不能为空");
        return false;
      }
      if (this.photoAlbum.albumCover === "") {
        this.$message.error("相册封面不能为空");
        return false;
      }
      axios.post("/api/album/saveOrUpdate", this.photoAlbum).then(res => {
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
        this.listAlbum()
      })
      this.addOrEdit = false;
    },
    searchAlbums() {
      this.current = 1;
      this.listAlbum();
    },
  },
  created() {
    this.listAlbum()
  }
}
</script>

<style scoped>
.album-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}

.album-cover::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.album-name {
  text-align: center;
  margin-top: 0.5rem;
}

.album-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.album-operation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}

.album-container {
  margin-top: 1.25rem;
}
</style>