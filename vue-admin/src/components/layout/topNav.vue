<template>
  <el-container>
    <el-aside width="auto" class="header-logo tap">
      <img class="logo" src="../../assets/logo.png" alt="Logo"/>
    </el-aside>
    <el-aside width="auto" class="header-logo tap">
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
                <back/>
              </el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-aside>
  </el-container>
</template>

<script>
import store from "../../store";

export default {
  name: 'navTop',
  data() {
    return {
      activeIndex: "1",
    };
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
      this.$router.push('/login')
    },
    goPage(page) {
      this.$router.push("/" + page)
    }
  },
  computed: {
    link() {
      return store.state.avatar
    }
  },
  watch: {}
};
</script>


<style scoped>
.el-aside {
  display: flex;
  justify-content: center;
  align-items: center;
}

section {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo {
  width: 200px;
}

.headerLogo, .logo {
  cursor: pointer;
}


</style>