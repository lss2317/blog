<template>
  <div>
    <!-- 导航栏 -->
    <div class="nav-bar">
      <!-- 折叠按钮 -->
      <div class="hambuger-container" @click="trigger">
        <el-icon>
          <Fold/>
        </el-icon>
      </div>
      <!-- 面包屑导航 -->
      <el-breadcrumb>
        <el-breadcrumb-item v-for="item of breadcrumbList" :key="item.path">
          <router-link :to="item.path">
            <span v-if="item.meta && item.meta.title">
              {{ item.meta.title }}
              <span class="breadcrumb-separator">/</span>
            </span>
            {{ item.name }}
          </router-link>
        </el-breadcrumb-item>
      </el-breadcrumb>
      <!-- 右侧菜单 -->
      <div class="right-menu">
        <!-- 全屏按钮 -->
        <div class="screen-full" @click="fullScreen">
          <el-icon>
            <FullScreen/>
          </el-icon>
        </div>
        <!-- 用户选项 -->
        <el-dropdown>
          <el-avatar icon="el-icon-user-solid" class="headerLogo" :src="link"></el-avatar>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goPage('setting')">
                <el-icon>
                  <user-filled/>
                </el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="signOut">
                <el-icon>
                  <SwitchButton/>
                </el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!-- 历史标签栏 -->
    <div class="tabs-view-container">
      <div class="tabs-wrapper">
           <span
               :class="isActive(item)"
               v-for="item of tabList"
               :key="item.path"
               @click="goTo(item)"
           >
             {{ item.name }}
             <el-icon
                 class="el-icon-close"
                 v-if="item.path !== '/main'"
                 @click.stop="removeTab(item)">
               <Close/>
             </el-icon>
           </span>
      </div>
      <div class="tabs-close-item" style="float:right" @click="closeAllTab">
        全部关闭
      </div>
    </div>
  </div>
</template>

<script>
import store from "../../store";
import {useRoute} from "vue-router"

export default {
  name: 'navTop',
  data() {
    return {
      activeIndex: "1",
      breadcrumbList: [],
    };
  },
  created() {
    //替换面包屑导航
    let matched = this.$route.matched.filter(item => item.name);
    const first = matched[0];
    if (first && first.name !== "首页") {
      matched = [{path: "/main", name: "首页"}].concat(matched);
    }
    this.breadcrumbList = matched;
    //保存当前页标签
    if (this.$route.name != null) {
      store.commit("saveTab", this.$route);
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    signOut() {
      window.localStorage.removeItem("admin_uuid_token")
      window.localStorage.removeItem("admin_login_token")
      window.localStorage.removeItem("userInfo")
      store.state.avatar = null
      //清除历史标签
      store.commit("resetTab");
      this.$router.push('/login')
    },
    goPage(page) {
      this.$router.push("/" + page)
    },
    trigger() {
      store.commit("trigger");
    },
    goTo(tab) {
      //跳转标签
      this.$router.push({path: tab.path});
    },
    removeTab(tab) {
      //删除标签
      store.commit("removeTab", tab);
      //如果删除的是当前页则返回上一标签页
      if (tab.path === this.$route.path) {
        let tabList = store.state.tabList;
        this.$router.push({path: tabList[tabList.length - 1].path});
      }
    },
    closeAllTab() {
      store.commit("resetTab");
      this.$router.push({path: "/"});
    },
    fullScreen() {
      let element = document.documentElement;
      if (this.fullscreen) {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen();
        }
      } else {
        if (element.requestFullscreen) {
          element.requestFullscreen();
        } else if (element.webkitRequestFullScreen) {
          element.webkitRequestFullScreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
          element.msRequestFullscreen();
        }
      }
      this.fullscreen = !this.fullscreen;
    }
  },
  computed: {
    link() {
      return store.state.avatar
    },
    isFold() {
      return store.state.collapse ? "el-icon-s-unfold" : "el-icon-s-fold";
    },
    //标签是否处于当前页
    isActive() {
      return function (tab) {
        const route = useRoute()
        if (tab.path === route.path) {
          return "tabs-view-item-active";
        }
        return "tabs-view-item";
      };
    },
    tabList() {
      return store.state.tabList
    }

  },
  watch: {}
};
</script>

<style scoped>
.nav-bar {
  display: flex;
  align-items: center;
  padding-left: 15px;
  padding-right: 30px;
  height: 50px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.hambuger-container {
  font-size: 1.25rem;
  cursor: pointer;
  margin-right: 24px;
}

.tabs-wrapper {
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  width: 95%;
}

.tabs-view-container {
  display: flex;
  position: relative;
  padding-left: 10px;
  padding-right: 10px;
  height: 33px;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);
}

.tabs-view-item {
  display: inline-block;
  cursor: pointer;
  height: 25px;
  line-height: 25px;
  border: 1px solid #d8dce5;
  color: #495060;
  background: #fff;
  padding: 0 8px;
  font-size: 12px;
  margin-top: 4px;
  margin-left: 5px;
}

.tabs-close-item {
  position: absolute;
  right: 10px;
  display: inline-block;
  cursor: pointer;
  height: 25px;
  line-height: 25px;
  border: 1px solid #d8dce5;
  color: #495060;
  background: #fff;
  padding: 0 8px;
  font-size: 12px;
  margin-top: 4px;
  margin-left: 5px;
}

.tabs-view-item-active {
  display: inline-block;
  cursor: pointer;
  height: 26px;
  line-height: 26px;
  padding: 0 8px;
  font-size: 12px;
  margin-top: 4px;
  margin-left: 5px;
  background-color: #42b983;
  color: #fff;
  border-color: #42b983;
}

.tabs-view-item-active:before {
  content: "";
  background: #fff;
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  /*position: relative;*/
  margin-right: 6px;
  vertical-align: middle;
}

.el-icon-close {
  padding: 0.1rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}

.el-icon-close:hover {
  border-radius: 50%;
  background: #b4bccc;
  transition-duration: 0.3s;
}

.right-menu {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.el-icon-caret-bottom {
  margin-left: 0.5rem;
  font-size: 0.75rem;
}

.screen-full {
  cursor: pointer;
  margin-right: 1rem;
  font-size: 1.25rem;
}

*::-webkit-scrollbar {
  width: 0.5rem;
  height: 6px;
}

*::-webkit-scrollbar-thumb {
  border-radius: 0.5rem;
  background-color: rgba(144, 147, 153, 0.3);
}

.breadcrumb-separator {
  margin: 0 9px;
  font-weight: 700;
  color: var(--el-text-color-placeholder);
}

.headerLogo {
  cursor: pointer;
}
</style>
