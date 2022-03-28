<template>
  <div class="reply-input-wrapper" style="display: none" ref="reply">
    <textarea
        class="comment-textarea"
        :placeholder="'回复 @' + nickname + '：'"
        auto-grow
        dense
        v-model="commentContent"
    />
    <div class="emoji-container">
      <span
          :class="chooseEmoji ? 'emoji-btn-active' : 'emoji-btn'"
          @click="chooseEmoji = !chooseEmoji"
      >
        <i class="iconfont iconbiaoqing"/>
      </span>
      <div style="margin-left:auto">
        <button @click="cancleReply" class="cancle-btn v-comment-btn">
          取消
        </button>
        <button @click="insertReply" class="upload-btn v-comment-btn">
          提交
        </button>
      </div>
    </div>
    <!-- 表情框 -->
    <emoji @addEmoji="addEmoji" :chooseEmoji="chooseEmoji"></emoji>
  </div>
</template>

<script>
import Emoji from "./Emoji";
import EmojiList from "../assets/js/emoji";

export default {
  components: {
    Emoji
  },
  props: {
    type: {
      type: Number
    }
  },
  data() {
    return {
      index: 0,
      chooseEmoji: false,
      nickname: "",
      replyUserId: null,
      parentId: null,
      commentContent: ""
    };
  },
  methods: {
    cancleReply() {
      this.$refs.reply.style.display = "none";
    },
    insertReply() {
      //判断登录
      if (!this.$store.state.userId) {
        this.$store.state.loginFlag = true;
        return false;
      }
      if (this.commentContent.trim() === "") {
        this.$toast({type: "error", message: "回复不能为空"});
        return false;
      }
      //解析表情
      let reg = /\[.+?\]/g;
      this.commentContent = this.commentContent.replace(reg, function (str) {
        return (
            "<img src= '" +
            EmojiList[str] +
            "' width='22'height='20' style='padding: 0 1px'/>"
        );
      });
      const path = this.$route.path;
      const arr = path.split("/");
      let comment = {
        userId: this.$store.state.userId,
        replyUserId: this.replyUserId,
        parentId: this.parentId,
        commentContent: this.commentContent,
        type: this.type
      };
      switch (this.type) {
        case 1:
          comment.articleId = arr[2];
          break;
        case 3:
          comment.talkId = arr[2];
          break;
        default:
          break;
      }
      this.commentContent = "";
      this.axios.post("/api/comment/saveComment", comment).then(res => {
        if (res.data.code === 200) {
          console.log(this.index)
          this.$emit("reloadReply", this.index);
          this.$toast({type: "success", message: "回复成功"});
        } else {
          this.$toast({type: "error", message: res.data.message});
        }
      });
    },
    addEmoji(text) {
      this.commentContent += text;
    }
  }
};
</script>

<style scoped>
.reply-input-wrapper {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 10px;
  margin: 0 0 10px;
}
</style>
