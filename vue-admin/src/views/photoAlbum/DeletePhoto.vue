<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
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
          @click="this.movePhoto = true"
          :disabled="selectPhotoIdList.length === 0"
          icon="SuccessFilled"
      >
        批量恢复
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
    <!-- 移动对话框 -->
    <el-dialog v-model="movePhoto" width="30%" title="恢复照片">
      <el-empty v-if="albumList.length === 0" description="暂无其他相册"/>
      <el-form v-else label-width="80px" size="medium" :model="photoForm">
        <el-radio-group v-model="albumId">
          <div class="album-check-item">
            <template v-for="item of albumList">
              <div style="height:70px">
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
import {ElMessageBox, ElNotification} from "element-plus";

export default {
  name: "PhotoRecycling",
  data() {
    return {
      loading: true,
      isIndeterminate: false,
      checkAll: false,
      photoList: [],
      photoIdList: [],
      selectPhotoIdList: [],
      albumId: null,
      albumList: [],
      movePhoto: false,
      current: 1,
      size: 8,
      count: 0,
      photoForm: {
        id: null,
        photoName: "",
        photoDesc: ""
      },
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
    listPhotos() {
      this.loading = true
      axios.get("/api/photo/listPhotos", {
        params: {
          currentPage: this.current,
          pageSize: this.size,
          isDelete: 1
        }
      }).then(res => {
        this.photoList = res.data.data.data
        this.count = res.data.data.count
        this.loading = false
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
    listAlbums() {
      axios.get("/api/album/listAlbums").then(res => {
        this.albumList = res.data.data
      })
    },
  },
  created() {
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
.operation {
  display: flex;
  justify-content: flex-end;
  margin-top: 2.25rem;
  margin-bottom: 2rem;
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
  margin-top: 0.3rem;
  text-align: center;
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

.album-check {
  display: flex;
  align-items: center;
}

.album-check-cover {
  border-radius: 4px;
  width: 8rem;
  height: 4rem;
}
</style>