<template>

  <el-container>
    <!-- header部分 -->
    <el-header>
      <topHeader></topHeader>
    </el-header>
    <el-container>
      <!-- aside部分 -->
      <leftSidebar></leftSidebar>
      <el-main>
        <div class="bread">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="this.$route.meta.title">{{this.$route.meta.title}}</el-breadcrumb-item>
            <el-breadcrumb-item v-if="this.$route.name">{{ this.$route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <!-- main部分 -->
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
    return {
    }
  },
}
</script>

<style scoped>
.el-main {
  background-color: #f5f7f9;
  padding: 0;
}

.bread {
  padding-left: 40px;
  padding-top: 20px;
  padding-bottom: 5px;
}

.el-header,
.el-footer {
  background-color: white;
  box-sizing: border-box;
  border-bottom: 1px solid #f5f1f1;
}

.el-container {
  height: 100%;
  overflow: hidden;
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
  overflow: hidden;
  padding: 12px 20px 20px;
}
</style>
