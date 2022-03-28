<template>
  <v-footer app padless absolute style="background:rgba(0,0,0,0)" v-if="!this.isMessage">
    <div class="footer-wrap">
      <div>
        ©{{ blogInfo.websiteConfig.websiteCreateTime | year }} -
        {{ new Date().getFullYear() }} By
        {{ blogInfo.websiteConfig.websiteAuthor }}
      </div>
      <a href="https://beian.miit.gov.cn/" target="_blank">
        琼ICP备2022001317号-1
      </a>
    </div>
  </v-footer>
</template>

<script>
export default {
  computed:{
    blogInfo() {
      return this.$store.state.blogInfo;
    },
    isMessage() {
      return this.$route.path === "/message";
    },
  }
}
</script>

<style scoped>
.footer {
  font-size: 0.75rem;
}

.footer div {
  line-height: 1.75;
}

.footer-wrap {
  width: 100%;
  line-height: 2;
  position: relative;
  padding: 40px 20px;
  color: #eee;
  font-size: 14px;
  text-align: center;
  background: linear-gradient(-45deg, #ee7752, #ce3e75, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: Gradient 10s ease infinite;
}

.footer-wrap a {
  color: #eee !important;
}

@keyframes Gradient {
  0% {
    background-position: 0 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
}
</style>
