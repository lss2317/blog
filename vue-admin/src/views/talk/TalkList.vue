<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 文章状态 -->
    <div class="status-menu">
      <span>状态</span>
      <span @click="changeStatus(null)" :class="this.isActive(null)">全部</span>
      <span @click="changeStatus(1)" :class="isActive(1)">
        公开
      </span>
      <span @click="changeStatus(2)" :class="isActive(2)">
        私密
      </span>
    </div>
    <el-empty v-if="talkList.length === 0" description="暂无说说"/>
    <!-- 说说列表 -->
    <div class="talk-item" v-for="item of talkList" :key="item.id">
      <!-- 用户信息 -->
      <div class="user-info-wrapper">
        <el-avatar class="user-avatar" :src="item.avatar" :size="36"/>
        <div class="user-detail-wrapper">
          <div class="user-nickname">
            <div>{{ item.nickname }}</div>
            <!-- 操作 -->
            <el-dropdown trigger="click" @command="handleCommand">
              <el-icon style="color:#333;cursor: pointer;">
                <more-filled />
              </el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="'1,' + item.id">
                    <el-icon>
                      <edit-pen/>
                    </el-icon>
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item :command="'2,' + item.id">
                    <el-icon>
                      <delete/>
                    </el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <!-- 发表时间 -->
          <div class="time">
            {{ item.createTime }}
            <span class="top" v-if="item.isTop === 1">
              <i class="iconfont el-icon-myzhiding"/> 置顶
            </span>
            <span class="secret" v-if="item.status === 2">
              <i class="iconfont el-icon-mymima"/> 私密
            </span>
          </div>
          <!-- 说说信息 -->
          <div class="talk-content" v-html="item.content"/>
          <!-- 图片列表 -->
          <el-row :gutter="4" class="talk-images" v-if="item.imgList">
            <el-col
                :md="8"
                :cols="6"
                v-for="(img, index) of item.imgList"
                :key="index"
            >
              <el-image
                  class="images-items"
                  :append-to-body='true'
                  :src="img"
                  :preview-src-list="[img]"
              />
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
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
  </el-card>
</template>

<script>
import axios from "axios";
import {ElMessageBox, ElNotification} from "element-plus"

export default {
  name: "TalkList",
  data() {
    return {
      status: null,
      current: 1,
      size: 5,
      count: 0,
      talkList: [],
      talkId: null,
    }
  },
  created() {
    this.listTalks()
  },
  methods: {
    changeStatus(status) {
      this.current = 1;
      this.status = status;
      this.listTalks()
    },
    sizeChange(size) {
      this.size = size
      this.listTalks()
    },
    currentChange(current) {
      this.current = current
      this.listTalks()
    },
    open(talkId) {
      ElMessageBox.confirm(
          '确定要删除该说说吗?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
      ).then(() => {
        //删除说说
        axios.post("/api/talk/deleteTalk", {
            id: talkId
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
          this.listTalks()
        })
      })
    }
    ,
    handleCommand(command) {
      let arr = command.split(",");
      this.talkId = arr[1];
      switch (arr[0]) {
        case "1":
          this.$router.push({
            name: "发布说说", params: {
              talkId: this.talkId
            }
          });
          break;
        case "2":
          this.open(this.talkId)
          break;
      }
    },
    listTalks() {
      axios.get("/api/talk/listTalks", {
        params: {
          currentPage: this.current,
          pageSize: this.size,
          status: this.status
        }
      }).then(res => {
        this.talkList = res.data.data.data
        this.count = res.data.data.count
      })
    }
  },
  computed: {
    isActive() {
      return function (status) {
        return this.status === status ? "active-status" : "status";
      };
    }
  },
}
</script>

<style scoped>
.status-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}

.status-menu span {
  margin-right: 24px;
}

.status {
  cursor: pointer;
}

.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}

.talk-item:not(:first-child) {
  margin-top: 20px;
}

.talk-item {
  padding: 16px 20px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition: all 0.3s ease 0s;
}

.talk-item:hover {
  box-shadow: 0 5px 10px 8px rgb(7 17 27 / 16%);
  transform: translateY(-3px);
}

.user-info-wrapper {
  width: 100%;
  display: flex;
}

.user-avatar {
  border-radius: 50%;
}

.user-avatar {
  transition: all 0.5s;
}

.user-avatar:hover {
  transform: rotate(360deg);
}

.user-detail-wrapper {
  margin-left: 10px;
  width: 100%;
}

.user-nickname {
  font-size: 15px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
}

.user-sign {
  margin-left: 4px;
}

.time {
  color: #999;
  margin-top: 2px;
  font-size: 12px;
}

.top {
  color: #ff7242;
  margin-left: 10px;
}

.secret {
  color: #999;
  margin-left: 10px;
}

.talk-content {
  margin-top: 8px;
  font-size: 14px;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}

.talk-images {
  margin-top: 8px;
}

.images-items {
  cursor: pointer;
  object-fit: cover;
  height: 200px;
  width: 100%;
  border-radius: 4px;
}
</style>