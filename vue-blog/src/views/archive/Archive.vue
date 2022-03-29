<template>
  <div>
    <!-- banner -->
    <div class="archive-banner banner" :style="cover">
      <h1 class="banner-title">归档</h1>
    </div>
    <!-- 归档列表 -->
    <v-card class="blog-container">
      <timeline>
        <timeline-title> 目前共计{{ count }}篇文章，继续加油</timeline-title>
        <timeline-item v-for="item of archiveList" :key="item.id">
          <!-- 日期 -->
          <span class="time">{{ item.createTime | date }}</span>
          <!-- 文章标题 -->
          <router-link
              :to="'/article/' + item.id"
              style="color:#666;text-decoration: none"
          >
            {{ item.articleTitle }}
          </router-link>
        </timeline-item>
      </timeline>
      <!-- 分页按钮 -->
      <v-pagination
          color="#00C4B6"
          v-model="current"
          :length="Math.ceil(count / pageSize)"
          total-visible="7"
      />
    </v-card>
  </div>
</template>

<script>
import {Timeline, TimelineItem, TimelineTitle} from "vue-cute-timeline";

export default {
  created() {
    this.listArchives();
  },
  components: {
    Timeline,
    TimelineItem,
    TimelineTitle
  },
  data: function () {
    return {
      current: 1,
      pageSize: 10,
      count: 0,
      archiveList: []
    };
  },
  methods: {
    listArchives() {
      this.axios
          .get("/api/article/archives", {
            params: {
              current: this.current,
              pageSize: this.pageSize
            }
          })
          .then(res => {
            this.archiveList = res.data.data.data;
            this.count = res.data.data.count;
          });
    }
  },
  computed: {
    cover() {
      let cover = "";
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel === "archive") {
          cover = item.pageCover;
        }
      });
      return "background: url(" + cover + ") center center / cover no-repeat";
    }
  },
  watch: {
    current(value) {
      this.axios
          .get("/api/article/archives", {
            params: {
              current: value,
              pageSize: this.pageSize
            }
          })
          .then(res => {
            this.archiveList = res.data.data.data;
            this.count = res.data.data.count;
          });
    }
  }
};
</script>

<style scoped>
.archive-banner {
  background: #49b1f5 no-repeat center center;
}

.time {
  font-size: 0.75rem;
  color: #555;
  margin-right: 1rem;
}
</style>
