<template>
  <el-aside width="205px" style="overflow-x: hidden;background: #304156">
    <el-row class="tac">
      <el-col>
        <el-menu
            router
            :default-active="this.$route.path"
            class="el-menu-vertical-demo"
            background-color="#304156"
            text-color="#BFCBD9"
            active-text-color="#409EFF"
        >
          <el-menu-item index="/main" @click="goPage('main')">
            <el-icon>
              <home-filled/>
            </el-icon>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-sub-menu index="2">
            <template #title>
              <el-icon>
                <tickets/>
              </el-icon>
              <span>文章管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/postArticle" @click="goPage('postArticle')">
                <el-icon>
                  <edit-pen/>
                </el-icon>
                <span slot="title">发布文章</span>
              </el-menu-item>
              <el-menu-item index="/articleList" @click="goPage('articleList')">
                <el-icon>
                  <document-copy/>
                </el-icon>
                <span slot="title">文章列表</span>
              </el-menu-item>
              <el-menu-item index="/classification" @click="goPage('classification')">
                <el-icon>
                  <Menu/>
                </el-icon>
                <span slot="title">分类管理</span>
              </el-menu-item>
              <el-menu-item index="/tag" @click="goPage('tag')">
                <el-icon>
                  <price-tag/>
                </el-icon>
                <span slot="title">标签管理</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon>
                <chat-dot-square/>
              </el-icon>
              <span>消息管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/comment" @click="goPage('comment')">
                <el-icon>
                  <chat-dot-square/>
                </el-icon>
                <span slot="title">评论管理</span>
              </el-menu-item>
              <el-menu-item index="/message" @click="goPage('message')">
                <el-icon>
                  <chat-line-square/>
                </el-icon>
                <span slot="title">留言管理</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="4">
            <template #title>
              <el-icon>
                <user/>
              </el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/user" @click="goPage('user')">
                <el-icon><stamp /></el-icon>
                <span slot="title">用户列表</span>
              </el-menu-item>
              <el-menu-item index="/online" @click="goPage('online')">
                <el-icon><place /></el-icon>
                <span slot="title">在线用户</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="5">
            <template #title>
              <el-icon>
                <notebook/>
              </el-icon>
              <span>日志管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/operationLog" @click="goPage('operationLog')">
                <el-icon>
                  <notebook/>
                </el-icon>
                <span slot="title">操作日志</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="6">
            <template #title>
              <el-icon>
                <comment/>
              </el-icon>
              <span>说说管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/postTalk" @click="goPage('postTalk')">
                <el-icon>
                  <promotion/>
                </el-icon>
                <span slot="title">发布说说</span>
              </el-menu-item>
              <el-menu-item index="/talkList" @click="goPage('talkList')">
                <el-icon><success-filled /></el-icon>
                <span slot="title">说说列表</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="7">
            <template #title>
              <el-icon>
                <picture-filled/>
              </el-icon>
              <span>相册管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/albumList" @click="goPage('albumList')">
                <el-icon>
                  <camera-filled/>
                </el-icon>
                <span slot="title">相册列表</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-sub-menu index="8">
            <template #title>
              <el-icon>
                <setting/>
              </el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/website" @click="goPage('website')">
                <el-icon><basketball /></el-icon>
                <span slot="title">网站管理</span>
              </el-menu-item>
              <el-menu-item index="/page" @click="goPage('page')">
                <el-icon>
                  <document-checked/>
                </el-icon>
                <span slot="title">页面管理</span>
              </el-menu-item>
              <el-menu-item index="/friendLink" @click="goPage('friendLink')">
                <el-icon>
                  <Link/>
                </el-icon>
                <span slot="title">友链管理</span>
              </el-menu-item>
              <el-menu-item index="/about" @click="goPage('about')">
                <el-icon><avatar /></el-icon>
                <span slot="title">关于我</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>

          <el-menu-item index="/setting" @click="goPage('setting')">
            <el-icon>
              <user-filled/>
            </el-icon>
            <span slot="title">个人中心</span>
          </el-menu-item>

        </el-menu>
      </el-col>
    </el-row>
  </el-aside>
</template>

<script>
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
}
</script>

<style scoped>
.el-row {
  height: 100%;
}

.el-menu {
  border-right: none;
  overflow: hidden;
}

.el-aside {
  border-right: 1px solid #f5f1f1;
}

.tac {
  overflow-y: auto;
  overflow-x: hidden;
  background: #304156;
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