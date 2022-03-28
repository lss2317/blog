import {createRouter, createWebHistory} from 'vue-router'
import Home from '../views/Home.vue'
import Main from "../views/main/Main";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        redirect: 'main',
        children: [
            {
                path: '/main',
                component: Main
            },
            {
                path: '/postArticle',
                name: '发布文章',
                component: () => import('../views/article/PostArticle')
            },
            {
                path: '/articleList',
                name: '文章列表',
                component: () => import('../views/article/ArticleList')
            },
            {
                path: '/tag',
                name: '标签管理',
                component: () => import('../views/article/Tag')
            },
            {
                path: 'classification',
                name: '分类管理',
                component: () => import('../views/article/Classification')
            },
            {
                path: 'message',
                name: "留言管理",
                component: () => import('../views/message/Message')
            },
            {
                path: 'comment',
                name: '评论管理',
                component: () => import('../views/message/Comment')
            },
            {
                path: 'user',
                name: '用户列表',
                component: () => import('../views/user/User')
            },
            {
                path: 'online',
                name: '在线用户',
                component: () => import('../views/user/Online')
            },
            {
                path: 'operationLog',
                name: '操作日志',
                component: () => import('../views/log/OperationLog')
            },
            {
                path: 'friendLink',
                name: "友链管理",
                component: () => import('../views/system/FriendLink')
            },
            {
                path: 'postTalk',
                name: '发布说说',
                component: () => import('../views/talk/PostTalk')
            },
            {
                path: 'talkList',
                name: "说说列表",
                component: () => import('../views/talk/TalkList')
            },
            {
                path: 'albumList',
                name: "相册列表",
                component: () => import('../views/photoAlbum/AlbumList'),
            },
            {
                path: '/albumList/:albumId',
                name: '照片管理',
                component: () => import('../views/photoAlbum/Photo')
            },
            {
                path: 'deletePhoto',
                name: "照片回收站",
                component: () => import('../views/photoAlbum/DeletePhoto')
            },
            {
                path: "website",
                name: "网站管理",
                component: () => import('../views/system/Website')
            },
            {
                path: "setting",
                name: "个人中心",
                component: () => import('../views/personal/Setting')
            },
            {
                path: "page",
                name: "页面管理",
                component: () => import('../views/system/Page')
            },
            {
                path: 'about',
                name: "关于我",
                component: () => import('../views/system/About')
            }
        ]
    },
    {
        path: '/login',
        component: () => import('../views/login/Login')
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
