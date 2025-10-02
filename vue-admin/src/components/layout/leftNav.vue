<template>
  <div>
    <el-menu
        router
        :default-active="this.$route.path"
        :collapse="collapse"
        class="side-nav-bar"
        background-color="#304156"
        text-color="#BFCBD9"
        active-text-color="#409EFF"
    >
      <!-- 首页菜单项 -->
      <el-menu-item index="/main">
        <el-icon>
          <home-filled/>
        </el-icon>
        <span slot="title">首页</span>
      </el-menu-item>

      <!-- 文章管理 -->
      <el-sub-menu index="2">
        <template #title>
          <el-icon>
            <tickets/>
          </el-icon>
          <span>文章管理</span>
        </template>
        <el-menu-item index="/postArticle">
          <el-icon>
            <edit-pen/>
          </el-icon>
          <span slot="title">发布文章</span>
        </el-menu-item>
        <el-menu-item index="/articleList">
          <el-icon>
            <document-copy/>
          </el-icon>
          <span slot="title">文章列表</span>
        </el-menu-item>
        <el-menu-item index="/classification">
          <el-icon>
            <Menu/>
          </el-icon>
          <span slot="title">分类管理</span>
        </el-menu-item>
        <el-menu-item index="/tag">
          <el-icon>
            <price-tag/>
          </el-icon>
          <span slot="title">标签管理</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 消息管理 -->
      <el-sub-menu index="3">
        <template #title>
          <el-icon>
            <chat-dot-square/>
          </el-icon>
          <span>消息管理</span>
        </template>
        <el-menu-item index="/comment">
          <el-icon>
            <chat-dot-square/>
          </el-icon>
          <span slot="title">评论管理</span>
        </el-menu-item>
        <el-menu-item index="/message">
          <el-icon>
            <chat-line-square/>
          </el-icon>
          <span slot="title">留言管理</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 用户管理 -->
      <el-sub-menu index="4">
        <template #title>
          <el-icon>
            <user/>
          </el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/user">
          <el-icon>
            <stamp/>
          </el-icon>
          <span slot="title">用户列表</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 日志管理 -->
      <el-sub-menu index="5">
        <template #title>
          <el-icon>
            <notebook/>
          </el-icon>
          <span>日志管理</span>
        </template>
        <el-menu-item index="/operationLog">
          <el-icon>
            <notebook/>
          </el-icon>
          <span slot="title">操作日志</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 说说管理 -->
      <el-sub-menu index="6">
        <template #title>
          <el-icon>
            <comment/>
          </el-icon>
          <span>说说管理</span>
        </template>
        <el-menu-item index="/postTalk">
          <el-icon>
            <promotion/>
          </el-icon>
          <span slot="title">发布说说</span>
        </el-menu-item>
        <el-menu-item index="/talkList">
          <el-icon>
            <success-filled/>
          </el-icon>
          <span slot="title">说说列表</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 相册管理 -->
      <el-sub-menu index="7">
        <template #title>
          <el-icon>
            <picture-filled/>
          </el-icon>
          <span>相册管理</span>
        </template>
        <el-menu-item index="/albumList">
          <el-icon>
            <camera-filled/>
          </el-icon>
          <span slot="title">相册列表</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 系统管理 -->
      <el-sub-menu index="8">
        <template #title>
          <el-icon>
            <setting/>
          </el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/website">
          <el-icon>
            <basketball/>
          </el-icon>
          <span slot="title">网站管理</span>
        </el-menu-item>
        <el-menu-item index="/page">
          <el-icon>
            <document-checked/>
          </el-icon>
          <span slot="title">页面管理</span>
        </el-menu-item>
        <el-menu-item index="/friendLink">
          <el-icon>
            <Link/>
          </el-icon>
          <span slot="title">友链管理</span>
        </el-menu-item>
        <el-menu-item index="/about">
          <el-icon>
            <avatar/>
          </el-icon>
          <span slot="title">关于我</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 个人中心 -->
      <el-menu-item index="/setting">
        <el-icon>
          <user-filled/>
        </el-icon>
        <span slot="title">个人中心</span>
      </el-menu-item>

    </el-menu>
  </div>


</template>

<script>
import store from "../../store";

let $this = {};
export default {
  name: "leftNav",
  data() {
    return {};
  },
  beforeCreate() {
    $this = this;
  },
  methods: {
    goPage(link) {
      //页面跳转
      $this.$router.push("/" + link)
    }
  },
  computed: {
    collapse() {
      return store.state.collapse
    },
  }
}
</script>

<style scoped>
.side-nav-bar:not(.el-menu--collapse) {
  width: 210px;
}

.side-nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  overflow-x: hidden;
  overflow-y: auto;
}

.side-nav-bar i {
  margin-right: 1rem;
}

*::-webkit-scrollbar {
  width: 0.5rem;
  height: 1px;
}

*::-webkit-scrollbar-thumb {
  border-radius: 0.5rem;
  background-color: rgba(144, 147, 153, 0.3);
}
</style>
