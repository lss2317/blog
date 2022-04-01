<template>
  <div>
    <!-- banner -->
    <div class="home-banner" :style="this.cover">
      <div class="banner-container">
        <h1 class="blog-title animated zoomIn">
          {{ blogInfo.websiteConfig.websiteName }}
        </h1>
        <div class="blog-intro">
          {{ obj.output }} <span class="typed-cursor">|</span>
        </div>
        <!--  联系方式-->
        <div class="blog-contact animated zoomIn">
          <a
              v-if="isShowSocial('qq')"
              class="ml-5 mr-5 iconfont iconqq"
              target="_blank"
              :href="
              'http://wpa.qq.com/msgrd?v=3&uin=' +
                blogInfo.websiteConfig.qq +
                '&site=qq&menu=yes'
            "
          />
          <a
              v-if="isShowSocial('github')"
              target="_blank"
              :href="blogInfo.websiteConfig.github"
              class="ml-5 mr-5 iconfont icongithub"
          />
          <a
              v-if="isShowSocial('gitee')"
              target="_blank"
              :href="blogInfo.websiteConfig.gitee"
              class="ml-5 mr-5 iconfont icongitee-fill-round"
          />
        </div>
      </div>
      <!-- 向下滚动 -->
      <div class="scroll-down" @click="scrollDown">
        <v-icon color="#fff" class="scroll-down-effects">
          mdi-chevron-down
        </v-icon>
      </div>
    </div>
    <!-- 主页文章 -->
    <v-row class="home-container">
      <v-col md="9" cols="12">
        <!-- 说说轮播 -->
        <v-card class="animated zoomIn" v-if="talkList.length > 0">
          <Swiper :list="talkList" />
        </v-card>
        <v-card
            class="animated zoomIn article-card"
            v-for="(item, index) in articleList"
            :key="item.id"
        >
          <!-- 文章封面图 -->
          <div :class="isRight(index)">
            <router-link :to="'/article/' + item.id">
              <v-img
                  class="on-hover"
                  width="100%"
                  height="100%"
                  :src="item.articleCover"
              />
            </router-link>
          </div>
          <!-- 文章信息 -->
          <div class="article-wrapper">
            <div style="line-height:1.4">
              <router-link :to="'/article/' + item.id">
                {{ item.articleTitle }}
              </router-link>
            </div>
            <div class="article-info">
              <!-- 是否置顶 -->
              <span v-if="item.isTop === 1">
                <span style="color:#ff7242">
                  <i class="iconfont iconzhiding"/> 置顶
                </span>
                <span class="separator">|</span>
              </span>
              <!-- 发表时间 -->
              <v-icon size="14">mdi-calendar-month-outline</v-icon>
              {{ item.createTime | date }}
              <span class="separator">|</span>
              <!-- 文章分类 -->
              <router-link :to="'/classification/' + item.classId">
                <v-icon size="14">mdi-inbox-full</v-icon>
                {{ item.classificationName }}
              </router-link>
              <span class="separator">|</span>
              <!-- 文章标签 -->
              <router-link
                  style="display:inline-block"
                  :to="'/tags/' + tag.tagId"
                  class="mr-1"
                  v-for="tag of item.tagNameList"
                  :key="tag.tagName"
              >
                <v-icon size="14">mdi-tag-multiple</v-icon>
                {{ tag.tagName }}
              </router-link>
            </div>
            <!-- 文章内容 -->
            <div class="article-content">
              {{ item.articleContent }}
            </div>
          </div>
        </v-card>
        <!-- 无限加载 -->
        <infinite-loading @infinite="infiniteHandler">
          <div slot="no-more"/>
        </infinite-loading>
      </v-col>
      <!-- 博主信息 -->
      <v-col md="3" cols="12" class="d-md-block d-none">
        <div class="blog-wrapper">
          <v-card class="animated zoomIn blog-card mt-5">
            <div class="author-wrapper">
              <!-- 博主头像 -->
              <v-avatar size="110">
                <img
                    class="author-avatar"
                    :src="blogInfo.websiteConfig.websiteAvatar"
                />
              </v-avatar>
              <div style="font-size: 1.375rem">
                {{ blogInfo.websiteConfig.websiteAuthor }}
              </div>
              <div style="font-size: 0.875rem;">
                {{ blogInfo.websiteConfig.websiteIntro }}
              </div>
            </div>
            <!-- 博客信息 -->
            <div class="blog-info-wrapper">
              <div class="blog-info-data">
                <router-link to="/archives">
                  <div style="font-size: 0.875rem">文章</div>
                  <div style="font-size: 1.25rem">
                    {{ blogInfo.articleCount }}
                  </div>
                </router-link>
              </div>
              <div class="blog-info-data">
                <router-link to="/classification">
                  <div style="font-size: 0.875rem">分类</div>
                  <div style="font-size: 1.25rem">
                    {{ blogInfo.classCount }}
                  </div>
                </router-link>
              </div>
              <div class="blog-info-data">
                <router-link to="/tags">
                  <div style="font-size: 0.875rem">标签</div>
                  <div style="font-size: 1.25rem">{{ blogInfo.tagCount }}</div>
                </router-link>
              </div>
            </div>
            <!-- 收藏按钮 -->
            <a class="collection-btn" @click="tip = true">
              <v-icon color="#fff" size="18" class="mr-1">mdi-bookmark</v-icon>
              加入书签
            </a>
            <div class="card-info-social">
              <a
                  v-if="isShowSocial('qq')"
                  class="mr-5 iconfont iconqq"
                  target="_blank"
                  :href="
                  'http://wpa.qq.com/msgrd?v=3&uin=' +
                    blogInfo.websiteConfig.qq +
                    '&site=qq&menu=yes'
                "
              />
              <a
                  target="_blank"
                  v-if="isShowSocial('github')"
                  :href="blogInfo.websiteConfig.github"
                  class="mr-5 iconfont icongithub"
              />
              <a
                  target="_blank"
                  v-if="isShowSocial('gitee')"
                  :href="blogInfo.websiteConfig.gitee"
                  class="mr-5 iconfont icongitee-fill-round"
              />
            </div>
          </v-card>
          <!-- 网站信息 -->
          <v-card class="blog-card animated zoomIn mt-5 big">
            <div class="web-info-title">
              <v-icon size="18">mdi-bell</v-icon>
              公告
            </div>
            <div style="font-size:0.875rem">
              {{ blogInfo.websiteConfig.websiteNotice }}
            </div>
          </v-card>
          <!-- 网站信息 -->
          <v-card class="blog-card animated zoomIn mt-5">
            <div class="web-info-title">
              <v-icon size="18">mdi-chart-line</v-icon>
              网站资讯
            </div>
            <div class="web-info">
              <div style="padding:4px 0 0">
                运行时间:<span class="float-right">{{ time }}</span>
              </div>
              <div style="padding:4px 0 0">
                总访问量:<span class="float-right">
                  {{ blogInfo.viewsCount }}
                </span>
              </div>
            </div>
          </v-card>
        </div>
      </v-col>
    </v-row>
    <!-- 提示消息 -->
    <v-snackbar v-model="tip" top color="#49b1f5" :timeout="2000">
      按CTRL+D 键将本页加入书签
    </v-snackbar>
  </div>
</template>

<script>
import EasyTyper from "easy-typer-js";
import Swiper from "@/components/Swiper";

export default {
  created() {
    this.init()
    this.listHomeTalks()
    this.timer = setInterval(this.runTime, 1000);
  },
  data() {
    return {
      tip: false,
      time: "",
      obj: {
        output: "",
        isEnd: false,
        speed: 300,
        singleBack: false,
        sleep: 0,
        type: "rollback",
        backSpeed: 40,
        sentencePause: true
      },
      articleList: [],
      talkList: [],
      current: 1
    };
  },
  methods: {
    init() {
      document.title = this.blogInfo.websiteConfig.websiteName;
      // 一言Api进行打字机循环输出效果
      fetch("https://v1.hitokoto.cn?c=i")
          .then(res => {
            return res.json();
          })
          .then(res => {
            this.initTyped(res.hitokoto);
          });

    },
    initTyped(input, fn, hooks) {
      const obj = this.obj;
      // eslint-disable-next-line no-unused-vars
      const typed = new EasyTyper(obj, input, fn, hooks);
    },
    // 初始化
    scrollDown() {
      window.scrollTo({
        behavior: "smooth",
        top: document.documentElement.clientHeight
      });
    },
    runTime() {
      let timeOld =
          new Date().getTime() -
          new Date(this.blogInfo.websiteConfig.websiteCreateTime).getTime();
      let msPerDay = 24 * 60 * 60 * 1000;
      let daySold = Math.floor(timeOld / msPerDay);
      let str = "";
      let day = new Date();
      str += daySold + "天";
      str += day.getHours() + "时";
      str += day.getMinutes() + "分";
      str += day.getSeconds() + "秒";
      this.time = str;
    },
    infiniteHandler($state) {
      let md = require("markdown-it")();
      this.axios
          .get("/api/article/articles", {
            params: {
              current: this.current
            }
          })
          .then(res => {
            // 去除markdown标签
            res.data.data.forEach(item => {
              item.articleContent = md
                  .render(item.articleContent)
                  .replace(/<\/?[^>]*>/g, "")
                  .replace(/[|]*\n/, "")
                  .replace(/&npsp;/gi, "");
              this.articleList.push(item);
              $state.loaded();
            });
            this.current++;
            $state.complete();
          });
    },
    listHomeTalks() {
      this.axios.get("/api/talk/getHomeTalks").then(res => {
        this.talkList = res.data.data;
      })
    },
  },
  computed: {
    isRight() {
      return function (index) {
        if (index % 2 === 0) {
          return "article-cover left-radius";
        }
        return "article-cover right-radius";
      };
    },
    isShowSocial() {
      return function (social) {
        return this.blogInfo.websiteConfig.socialUrlList.indexOf(social) !== -1;
      };
    },
    cover() {
      let cover = "";
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel === "home") {
          cover = item.pageCover;
        }
      });
      return (
          "background: #49b1f5 url(" + cover + ") center center / cover no-repeat"
      );
    },
    // eslint-disable-next-line vue/no-dupe-keys
    blogInfo() {
      return this.$store.state.blogInfo;
    }
  },
  components: {
    Swiper
  }
};
</script>

<style lang="stylus">
.typed-cursor
  opacity: 1
  animation: blink 0.7s infinite

@keyframes blink
  0%
    opacity: 1
  50%
    opacity: 0
  100%
    opacity: 1
</style>

<style scoped>
.home-banner {
  position: absolute;
  top: -58px;
  left: 0;
  right: 0;
  height: 100vh;
  background-attachment: fixed;
  text-align: center;
  color: #fff !important;
  animation: header-effect 1s;
}

.banner-container {
  margin-top: 43vh;
  line-height: 1.5;
  color: #eee;
}

.blog-contact a {
  color: #fff !important;
}

.card-info-social {
  line-height: 40px;
  text-align: center;
  margin: 6px 0 -6px;
}

.card-info-social a {
  font-size: 1.5rem;
}

.left-radius {
  border-radius: 8px 0 0 8px !important;
  order: 0;
}

.right-radius {
  border-radius: 0 8px 8px 0 !important;
  order: 1;
}

.article-wrapper {
  font-size: 14px;
}

@media (min-width: 760px) {
  .blog-title {
    font-size: 2.5rem;
  }

  .blog-intro {
    font-size: 1.5rem;
  }

  .blog-contact {
    display: none;
  }

  .home-container {
    max-width: 1200px;
    margin: calc(100vh - 48px) auto 28px auto;
    padding: 0 5px;
  }

  .article-card {
    display: flex;
    align-items: center;
    height: 280px;
    width: 100%;
    margin-top: 20px;
  }

  .article-cover {
    overflow: hidden;
    height: 100%;
    width: 45%;
  }

  .on-hover {
    transition: all 0.6s;
  }

  .article-card:hover .on-hover {
    transform: scale(1.1);
  }

  .article-wrapper {
    padding: 0 2.5rem;
    width: 55%;
  }

  .article-wrapper a {
    font-size: 1.5rem;
    transition: all 0.3s;
  }
}

@media (max-width: 759px) {
  .blog-title {
    font-size: 26px;
  }

  .blog-contact {
    font-size: 1.25rem;
    line-height: 2;
  }

  .home-container {
    width: 100%;
    margin: calc(100vh - 66px) auto 0 auto;
  }

  .article-card {
    margin-top: 1rem;
  }

  .article-cover {
    border-radius: 8px 8px 0 0 !important;
    height: 230px !important;
    width: 100%;
  }

  .article-cover div {
    border-radius: 8px 8px 0 0 !important;
  }

  .article-wrapper {
    padding: 1.25rem 1.25rem 1.875rem;
  }

  .article-wrapper a {
    font-size: 1.25rem;
    transition: all 0.3s;
  }
}

.scroll-down {
  cursor: pointer;
  position: absolute;
  bottom: 0;
  width: 100%;
}

.scroll-down i {
  font-size: 2rem;
}

.article-wrapper a:hover {
  color: #8e8cd8;
}

.article-info {
  font-size: 95%;
  color: #858585;
  line-height: 2;
  margin: 0.375rem 0;
}

.article-info a {
  font-size: 95%;
  color: #858585 !important;
}

.article-content {
  line-height: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.blog-wrapper {
  position: sticky;
  top: 10px;
}

.blog-card {
  line-height: 2;
  padding: 1.25rem 1.5rem;
}

.author-wrapper {
  text-align: center;
}

.blog-info-wrapper {
  display: flex;
  justify-self: center;
  padding: 0.875rem 0;
}

.blog-info-data {
  flex: 1;
  text-align: center;
}

.blog-info-data a {
  text-decoration: none;
}

.collection-btn {
  text-align: center;
  z-index: 1;
  font-size: 14px;
  position: relative;
  display: block;
  background-color: #49b1f5;
  color: #fff !important;
  height: 32px;
  line-height: 32px;
  transition-duration: 1s;
  transition-property: color;
}

.collection-btn:before {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: -1;
  background: #ff7242;
  content: "";
  transition-timing-function: ease-out;
  transition-duration: 0.5s;
  transition-property: transform;
  transform: scaleX(0);
  transform-origin: 0 50%;
}

.collection-btn:hover:before {
  transition-timing-function: cubic-bezier(0.45, 1.64, 0.47, 0.66);
  transform: scaleX(1);
}

.author-avatar {
  transition: all 0.5s;
}

.author-avatar:hover {
  transform: rotate(360deg);
}

.web-info {
  padding: 0.25rem;
  font-size: 0.875rem;
}

.scroll-down-effects {
  color: #eee !important;
  text-align: center;
  text-shadow: 0.1rem 0.1rem 0.2rem rgba(0, 0, 0, 0.15);
  line-height: 1.5;
  display: inline-block;
  text-rendering: auto;
  -webkit-font-smoothing: antialiased;
  animation: scroll-down-effect 1.5s infinite;
}

@keyframes scroll-down-effect {
  0% {
    top: 0;
    opacity: 0.4;
    filter: alpha(opacity=40);
  }
  50% {
    top: -16px;
    opacity: 1;
    filter: none;
  }
  100% {
    top: 0;
    opacity: 0.4;
    filter: alpha(opacity=40);
  }
}

.big i {
  color: #f00;
  animation: big 0.8s linear infinite;
}

@keyframes big {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}
</style>
