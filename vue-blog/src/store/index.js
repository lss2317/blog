import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        searchFlag: false,
        loginFlag: false,
        registerFlag: false,
        forgetFlag: false,
        emailFlag: false,
        drawer: false,
        loginUrl: "",
        userId: null,
        avatar: null,
        nickname: null,
        intro: null,
        webSite: null,
        articleLikeSet: [],
        commentLikeSet: [],
        talkLikeSet: [],
        blogInfo: {},
    },
    mutations: {
        login(state, user) {
            state.userId = user.id;
            state.avatar = user.avatar;
            state.nickname = user.nickname;
            state.intro = user.intro;
            state.webSite = user.website;
            state.articleLikeSet = user.articleLikeSet ? user.articleLikeSet : [];
            state.commentLikeSet = user.commentLikeSet ? user.commentLikeSet : [];
            state.talkLikeSet = user.talkLikeSet ? user.talkLikeSet : [];
            state.email = user.email;
            state.loginType = user.loginType;
        },
        logout(state) {
            state.userId = null;
            state.avatar = null;
            state.nickname = null;
            state.intro = null;
            state.webSite = null;
            state.articleLikeSet = [];
            state.commentLikeSet = [];
            state.talkLikeSet = [];
        },
        saveLoginUrl(state, url) {
            state.loginUrl = url;
        },
        updateUserInfo(state, user) {
            state.nickname = user.nickname;
            state.intro = user.intro;
            state.webSite = user.website;
        },
        updateAvatar(state, avatar) {
            state.avatar = avatar;
        },
        checkBlogInfo(state, blogInfo) {
            state.blogInfo = blogInfo;
        },
        closeModel(state) {
            state.registerFlag = false;
            state.loginFlag = false;
            state.searchFlag = false;
            state.emailFlag = false;
        },
        articleLike(state, articleId) {
            let articleLikeSet = state.articleLikeSet;
            if (articleLikeSet.indexOf(articleId) !== -1) {
                articleLikeSet.splice(articleLikeSet.indexOf(articleId), 1);
            } else {
                articleLikeSet.push(articleId);
            }
        },
        commentLike(state, commentId) {
            let commentLikeSet = state.commentLikeSet;
            if (commentLikeSet.indexOf(commentId) !== -1) {
                commentLikeSet.splice(commentLikeSet.indexOf(commentId), 1);
            } else {
                commentLikeSet.push(commentId);
            }
        },
        talkLike(state, talkId) {
            let talkLikeSet = state.talkLikeSet;
            if (talkLikeSet.indexOf(talkId) !== -1) {
                talkLikeSet.splice(talkLikeSet.indexOf(talkId), 1);
            } else {
                talkLikeSet.push(talkId);
            }
        }
    },
    actions: {},
    modules: {},
    plugins: [
        createPersistedState({
            storage: window.sessionStorage
        })
    ]
});
