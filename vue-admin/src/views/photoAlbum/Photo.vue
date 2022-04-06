<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <!-- 相册信息 -->
    <div class="album-info">
      <el-image fit="cover" class="album-cover" :preview-src-list="[albumInfo.albumCover]" :src="albumInfo.albumCover"/>
      <div class="album-detail">
        <div style="margin-bottom:0.6rem">
          <span class="album-name">{{ albumInfo.albumName }}</span>
          <span class="photo-count">{{ albumInfo.photoCount }}张</span>
        </div>
        <div>
          <span v-if="albumInfo.albumDesc" class="album-desc">
            {{ albumInfo.albumDesc }}
          </span>
          <el-button
              icon="picture"
              type="primary"
              @click="uploadPhoto = true"
          >
            上传照片
          </el-button>
        </div>
      </div>
      <!-- 相册操作 -->
      <div class="operation">
        <div class="all-check">
          <el-checkbox
              :indeterminate="isIndeterminate"
              v-model="checkAll"
              @change="handleCheckAllChange"
          >
            全选
          </el-checkbox>
          <div class="check-count">已选择{{ selectPhotoIdList.length }}张</div>
        </div>
        <el-button
            type="success"
            @click="movePhoto = true"
            :disabled="selectPhotoIdList.length === 0"
            icon="check"
        >
          移动到
        </el-button>
        <el-button
            type="danger"
            @click="deletePhotos"
            :disabled="selectPhotoIdList.length === 0"
            icon="delete"
        >
          批量删除
        </el-button>
      </div>
    </div>
    <!-- 空状态 -->
    <el-empty v-if="photoList.length === 0" description="暂无照片"/>
    <!-- 照片列表 -->
    <el-row class="photo-container" :gutter="12" v-loading="loading">
      <el-col :md="6" v-for="item of photoList" :key="item.id">
        <div class="photo-item">
          <div class="left-photo">
            <el-checkbox-group
                v-model="selectPhotoIdList"
                @change="handleCheckedPhotoChange"
            >
              <el-checkbox :label="item.id">
                &nbsp;
              </el-checkbox>
            </el-checkbox-group>
          </div>
          <!-- 照片操作 -->
          <div class="photo-operation">
            <el-dropdown @command="handleCommand">
              <el-icon style="color:#fff">
                <more-filled/>
              </el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="JSON.stringify(item)">
                    <el-icon>
                      <edit-pen/>
                    </el-icon>
                    编辑
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <el-image
              fit="cover"
              class="photo-img"
              :src="item.photoSrc"
              :preview-src-list="[item.photoSrc]"
          />
          <div class="photo-name" :style="this.color(item.id)">{{ item.photoName }}</div>
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
    <!-- 上传模态框 -->
    <el-dialog v-model="uploadPhoto" width="70%" top="10vh" title="上传照片">
      <!-- 上传 -->
      <div class="upload-container">
        <el-upload
            v-show="uploadList.length > 0"
            action="/api/files/upload"
            list-type="picture-card"
            :file-list="uploadList"
            multiple
            :before-upload="beforeUpload"
            :on-success="upload"
            :on-remove="handleRemove"
        >
          <el-icon>
            <Plus/>
          </el-icon>
        </el-upload>
        <div class="upload">
          <el-upload
              v-show="uploadList.length === 0"
              drag
              action="/api/files/upload"
              multiple
              :before-upload="beforeUpload"
              :on-success="upload"
              :show-file-list="false"
          >
            <el-icon class="el-icon--upload">
              <upload-filled/>
            </el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持上传jpg/png文件
              </div>
            </template>
          </el-upload>
        </div>
      </div>
      <div slot="footer">
        <div class="upload-footer">
          <div class="upload-count">共上传{{ uploadList.length }}张照片</div>
          <div style="margin-left:auto">
            <el-button @click="uploadPhoto = false">取 消</el-button>
            <el-button
                @click="savePhotos"
                type="primary"
                :disabled="uploadList.length === 0"
            >
              开始上传
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 编辑对话框 -->
    <el-dialog v-model="editPhoto" width="30%" title="修改信息">
      <el-form label-width="80px" size="medium" :model="photoForm">
        <el-form-item label="照片名称">
          <el-input style="width:220px" v-model="photoForm.photoName"/>
        </el-form-item>
        <el-form-item label="照片描述">
          <el-input style="width:220px" v-model="photoForm.photoDesc"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editPhoto = false">取 消</el-button>
        <el-button type="primary" @click="updatePhoto">
          确 定
        </el-button>
      </template>
    </el-dialog>
    <!-- 移动对话框 -->
    <el-dialog v-model="movePhoto" width="30%" title="移动照片">
      <el-empty v-if="albumList.length < 2" description="暂无其他相册"/>
      <el-form v-else label-width="80px" size="medium" :model="photoForm">
        <el-radio-group v-model="albumId">
          <div class="album-check-item">
            <template v-for="item of albumList">
              <div style="height:70px" v-if="item.id !== albumInfo.id">
                <el-radio
                    :key="item.id"
                    :label="item.id"
                >
                  <div class="album-check">
                    <el-image
                        fit="cover"
                        class="album-check-cover"
                        :src="item.albumCover"
                    />
                    <div style="margin-left:0.5rem">{{ item.albumName }}</div>
                  </div>
                </el-radio>
              </div>
            </template>
          </div>
        </el-radio-group>
      </el-form>
      <template #footer>
        <el-button @click="movePhoto = false">取 消</el-button>
        <el-button
            :disabled="albumId == null"
            type="primary"
            @click="updatePhotoAlbum"
        >
          确 定
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
import axios from "axios";
import config from "../../assets/js/config";
import {ElMessageBox, ElNotification} from "element-plus";
import * as imageConversion from 'image-conversion'

export default {
  name: "Photo",
  data() {
    return {
      loading: true,
      checkAll: false,
      isIndeterminate: false,
      uploadPhoto: false,
      editPhoto: false,
      movePhoto: false,
      uploadList: [],
      photoList: [],
      photoIdList: [],
      selectPhotoIdList: [],
      albumList: [],
      albumInfo: {
        id: null,
        albumName: "",
        albumDesc: "",
        albumCover: "",
        photoCount: 0
      },
      photoForm: {
        id: null,
        photoName: "",
        photoDesc: ""
      },
      albumId: null,
      current: 1,
      size: 8,
      count: 0
    }
  },
  methods: {
    sizeChange(size) {
      this.size = size
      this.listPhotos()
    },
    currentChange(current) {
      this.current = current
      this.listPhotos()
    },
    handleCommand(command) {
      this.photoForm = JSON.parse(command);
      this.editPhoto = true;
    },
    getAlbumInfo() {
      if (this.$route.params.albumId) {
        axios.get("/api/album/" + this.$route.params.albumId).then(res => {
          this.albumInfo = res.data.data.data
          this.albumInfo.photoCount = res.data.data.count
        })
      }
    },
    upload(response) {
      this.uploadList.push({url: response})
    },
    beforeUpload(file) {
      return new Promise(resolve => {
        if (file.size / 1024 < config.UPLOAD_SIZE) {
          resolve(file);
        }
        // 压缩到200KB,这里的200就是要压缩的大小,可自定义
        imageConversion
            .compressAccurately(file, config.UPLOAD_SIZE)
            .then(res => {
              resolve(res);
            });
      });
    },
    handleRemove(file) {
      axios.get("/api/files/delete", {
        params: {
          key: file.url
        }
      })
      this.uploadList.forEach((item, index) => {
        if (item.url === file.url) {
          this.uploadList.splice(index, 1);
        }
      });
    },
    savePhotos() {
      let photoUrlList = [];
      this.uploadList.forEach(item => {
        photoUrlList.push(item.url);
      });
      axios.post("/api/photo/savePhotos", {
        albumId: this.$route.params.albumId,
        photoList: photoUrlList,
      }).then(res => {
        if (res.data.code === 200) {
          ElNotification.success({
            title: "成功",
            message: res.data.message
          })
          this.uploadList = []
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
        this.listPhotos()
      })
      this.uploadPhoto = false
    },
    listPhotos() {
      this.loading = true
      axios.get("/api/photo/listPhotos", {
        params: {
          currentPage: this.current,
          pageSize: this.size,
          albumId: this.$route.params.albumId,
          isDelete: 0
        }
      }).then(res => {
        this.photoList = res.data.data.data
        this.count = res.data.data.count
        this.albumInfo.photoCount = res.data.data.count
        this.loading = false
      })
    },
    listAlbums() {
      axios.get("/api/album/listAlbums").then(res => {
        this.albumList = res.data.data
      })
    },
    handleCheckAllChange(val) {
      this.selectPhotoIdList = val ? this.photoIdList : [];
      this.isIndeterminate = false;
    },
    handleCheckedPhotoChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.photoIdList.length;
      this.isIndeterminate =
          checkedCount > 0 && checkedCount < this.photoIdList.length;
    },
    deletePhotos() {
      ElMessageBox.confirm(
          '是否删除选中照片?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        axios.delete("/api/photo/deletePhotos", {
          data: this.selectPhotoIdList
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
            return
          }
          this.listPhotos()
        })
      }).catch(() => {
      })
    },
    updatePhotoAlbum() {
      axios.put("/api/photo/updatePhotos", {
        albumId: this.albumId,
        selectPhotoIdList: this.selectPhotoIdList
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
          return
        } else if (res.data.code === 500) {
          ElNotification.warning({
            title: "错误",
            message: res.data.message
          })
        }
        this.movePhoto = false
        this.listPhotos()
      })
    },
    updatePhoto() {
      axios.put("/api/photo/updatePhoto", this.photoForm).then(res => {
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
          return
        } else if (res.data.code === 500) {
          ElNotification.warning({
            title: "错误",
            message: res.data.message
          })
        }
        this.editPhoto = false
        this.listPhotos()
      })
    }
  },
  created() {
    this.getAlbumInfo()
    this.listPhotos()
    this.listAlbums()
  },
  watch: {
    photoList() {
      this.photoIdList = [];
      this.photoList.forEach(item => {
        this.photoIdList.push(item.id);
      });
    }
  },
  computed: {
    color() {
      return function (id) {
        return this.selectPhotoIdList.findIndex(item => item === id) === -1 ? "" : "color:  #53a8ff"
      };
    }
  }
}
</script>

<style scoped>
.album-info {
  display: flex;
  margin-top: 2.25rem;
  margin-bottom: 2rem;
}

.album-cover {
  border-radius: 4px;
  width: 5rem;
  height: 5rem;
}

.album-check-cover {
  border-radius: 4px;
  width: 8rem;
  height: 4rem;
}

.album-detail {
  padding-top: 0.4rem;
  margin-left: 0.8rem;
}

.album-desc {
  font-size: 14px;
  margin-right: 0.8rem;
}

.operation {
  padding-top: 1.5rem;
  margin-left: auto;
}

.all-check {
  display: inline-flex;
  align-items: center;
  margin-right: 1rem;
}

.check-count {
  margin-left: 1rem;
  font-size: 12px;
}

.album-name {
  font-size: 1.25rem;
}

.photo-count {
  font-size: 12px;
  margin-left: 0.5rem;
}

.photo-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.photo-img {
  width: 100%;
  border-radius: 4px;
  height: 170px;
  position: relative;
}

.photo-name {
  font-size: 14px;
  text-align: center;
  margin-top: 0.3rem;
}

.upload-container {
  height: 400px;
}

.upload {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-footer {
  display: flex;
  align-items: center;
}

.photo-operation {
  position: absolute;
  z-index: 1000;
  top: 0.3rem;
  right: 0.5rem;
}

.album-check {
  display: flex;
  align-items: center;
}

.photo-container {
  margin-top: 1.25rem;
}

.left-photo {
  position: absolute;
  z-index: 1000;
  top: 0.3rem;
  left: 0.5rem;
}
</style>