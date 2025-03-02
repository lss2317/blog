<template>
  <div>
    <!-- 验证码容器 -->
    <div :id="captchaContainerId"></div>
  </div>
</template>

<script>
import config from "@/assets/js/config";
import axios from "axios";

export default {
  name: "AliyunCaptcha",
  data() {
    return {
      captchaContainerId: `captcha-container-${Date.now()}`, // 动态生成唯一容器 ID
      captchaInstance: null, // 保存验证码实例
    };
  },
  mounted() {
    window.initAliyunCaptcha({
      SceneId: config.ALIYUN_SceneId, // 场景ID。根据步骤二新建验证场景后，您可以在验证码场景列表，获取该场景的场景ID
      prefix: config.ALIYUN_PREFIX, // 身份标。开通阿里云验证码2.0后，您可以在控制台概览页面的实例基本信息卡片区域，获取身份标
      mode: 'popup', // 验证码模式。popup表示要集成的验证码模式为弹出式。无需修改
      element: `#${this.captchaContainerId}`, // 页面上预留的渲染验证码的元素，与原代码中预留的页面元素保持一致。
      captchaVerifyCallback: this.captchaVerifyCallback, // 业务请求(带验证码校验)回调函数，无需修改
      onBizResultCallback: this.onBizResultCallback, // 业务请求结果回调函数，无需修改
      getInstance: this.getInstance, // 绑定验证码实例函数，无需修改
      slideStyle: {
        width: 360,
        height: 40,
      }, // 滑块验证码样式，支持自定义宽度和高度，单位为px。其中，width最小值为320 px
      language: 'cn', // 验证码语言类型，支持简体中文（cn）、繁体中文（tw）、英文（en）
    });
  },
  methods: {
    initCaptcha() {
      console.log('验证码组件已挂载')
      window.initAliyunCaptcha({
        SceneId: config.ALIYUN_SceneId, // 场景ID。根据步骤二新建验证场景后，您可以在验证码场景列表，获取该场景的场景ID
        prefix: config.ALIYUN_PREFIX, // 身份标。开通阿里云验证码2.0后，您可以在控制台概览页面的实例基本信息卡片区域，获取身份标
        mode: 'popup', // 验证码模式。popup表示要集成的验证码模式为弹出式。无需修改
        element: `#${this.captchaContainerId}`, // 页面上预留的渲染验证码的元素，与原代码中预留的页面元素保持一致。
        captchaVerifyCallback: this.captchaVerifyCallback, // 业务请求(带验证码校验)回调函数，无需修改
        onBizResultCallback: this.onBizResultCallback, // 业务请求结果回调函数，无需修改
        getInstance: this.getInstance, // 绑定验证码实例函数，无需修改
        slideStyle: {
          width: 360,
          height: 40,
        }, // 滑块验证码样式，支持自定义宽度和高度，单位为px。其中，width最小值为320 px
        language: 'cn', // 验证码语言类型，支持简体中文（cn）、繁体中文（tw）、英文（en）
      });
    },
    async captchaVerifyCallback(captchaVerifyParam) {
      console.log(captchaVerifyParam);
      //向后端发起业务请求，获取验证码验证结果和业务结果
      const res = await axios.post("/api/user/CheckVerificationCode", {
        captchaVerifyParam: captchaVerifyParam
      });
      const flag = res.data.toString() === "true"; // 将字符串转换为布尔值
      if (flag) {
        return {
          captchaResult: true, // 验证码验证是否通过，boolean类型，必选
          bizResult: true, // 业务验证是否通过，boolean类型，可选；若为无业务验证结果的场景，bizResult可以为空
        }
      } else {
        return {
          captchaResult: false,
          bizResult: false,
        }
      }
    },
    onBizResultCallback(result) {
      this.$emit('success', result); // 触发业务结果事件
    },
    getInstance(instance) {
      this.captchaInstance = instance; // 保存验证码实例
      this.captchaInstance.show()
    },
  },
};
</script>

<style scoped>

</style>
