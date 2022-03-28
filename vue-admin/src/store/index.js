import {createStore} from 'vuex'

import createPersistedState from "vuex-persistedstate";


export default createStore({
    state: {
        avatar: null
    },
    mutations: {
        changeAvatar(state, user) {
            state.avatar = user.avatar
        }
    },
    actions: {},
    modules: {},
    plugins: [createPersistedState({
        key: "userInfo"
    })]
})
