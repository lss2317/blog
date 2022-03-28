<template>
  <div>
    <!-- banner -->
    <div class="tag-banner banner" :style="cover">
      <h1 class="banner-title">标签</h1>
    </div>
    <!-- 标签列表 -->
    <v-card class="blog-container">
      <div class="tag-cloud-title">标签 - {{ count }}</div>
      <div class="tag-cloud">
        <router-link
            :style="{ 'font-size': Math.floor(Math.random() * 10) + 18 + 'px' }"
            v-for="item of tagList"
            :key="item.tagId"
            :to="'/tags/' + item.id"
        >
          {{ item.tagName }}
        </router-link>
      </div>
    </v-card>
  </div>
</template>

<script>
export default {
  created() {
    this.listTags();
  },
  data: function () {
    return {
      tagList: [],
      count: 0
    };
  },
  methods: {
    listTags() {
      this.axios.get("/api/tag").then(res => {
        this.tagList = res.data.data;
        this.count = res.data.data.length;
      });
    }
  },
  computed: {
    cover() {
      let cover = "";
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel === "tag") {
          cover = item.pageCover;
        }
      });
      return "background: #49b1f5 url(" + cover + ") center center / cover no-repeat";
    }
  }
};
</script>

<style scoped>
.tag-banner {
  background: #49b1f5;
}

.tag-cloud-title {
  line-height: 2;
  font-size: 36px;
  text-align: center;
}

@media (max-width: 759px) {
  .tag-cloud-title {
    font-size: 25px;
  }
}

.tag-cloud {
  text-align: center;
}

.tag-cloud a {
  color: #616161;
  display: inline-block;
  text-decoration: none;
  padding: 0 8px;
  line-height: 2;
  transition: all 0.3s;
}

.tag-cloud a:hover {
  color: #03a9f4 !important;
  transform: scale(1.1);
}
</style>
