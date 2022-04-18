import {createStore} from 'vuex'

import createPersistedState from "vuex-persistedstate";


export default createStore({
    state: {
        avatar: null,
        draft: false,
        article: {},
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
        }
    },
    actions: {},
    modules: {},
    plugins: [createPersistedState({
        key: "userInfo"
    })]
})
