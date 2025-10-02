import {createStore} from 'vuex'

import createPersistedState from "vuex-persistedstate";


export default createStore({
    state: {
        avatar: null,
        draft: false,
        article: {},
        collapse: false,
        tabList: [{ name: "首页", path: "/main"}],
    },
    mutations: {
        changeAvatar(state, user) {
            state.avatar = user.avatar
        },
        saveDraft(state, article) {
            state.article = article;
            state.draft = true
        },
        dropDraft(state) {
            state.draft = false
            state.article = {}
        },
        trigger(state) {
            state.collapse = !state.collapse;
        },
        saveTab(state, tab) {
            if (state.tabList.findIndex(item => item.path === tab.path) === -1) {
                state.tabList.push({ name: tab.name, path: tab.path });
            }
        },
        removeTab(state, tab) {
            let index = state.tabList.findIndex(item => item.name === tab.name);
            state.tabList.splice(index, 1);
        },
        resetTab(state) {
            state.tabList = [{ name: "首页", path: "/main" }];
        },
    },
    actions: {},
    modules: {},
    plugins: [createPersistedState({
        key: "userInfo"
    })]
})
