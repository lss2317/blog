<template>
  <el-container>
    <!-- 侧边栏 -->
    <el-aside width="auto">
      <leftSidebar></leftSidebar>
    </el-aside>
    <el-container :class="'main-container ' + isHide">
      <!-- 头部导航栏 -->
      <el-header height="84px" style="padding:0">
        <topHeader :key="$route.fullPath"></topHeader>
      </el-header>
      <!-- main部分 -->
      <el-main style="background:#F7F9FB">
        <div class="fade-transform-box">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component"/>
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import topHeader from '../components/layout/topNav'
import leftSidebar from '../components/layout/leftNav'

export default {
  name: 'Home',
  components: {
    topHeader,
    leftSidebar
  },
  data() {
    return {}
  },
  computed: {
    isHide() {
      return this.$store.state.collapse ? "hideSideBar" : "";
    }
  }
}
</script>

<style scoped>
.main-container {
  transition: margin-left 0.45s;
  margin-left: 210px;
  min-height: 100vh;
}

.hideSideBar {
  margin-left: 64px;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s ease 0s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-box {
  position: relative;
  top: 0;
  bottom: 0;
  width: 100%;
  overflow: hidden;
}
</style>
