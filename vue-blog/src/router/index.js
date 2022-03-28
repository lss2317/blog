import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
    {
        path: "/",
        component: () => import('../views/home/Home')
    },
    {
        path: "/articles/:articleId",
        component: () => import('../views/article/Article')
    },
    {
        path: "/archives",
        component: () => import('../views/archive/Archive'),
        meta: {
            title: "归档"
        }
    },
    {
        path: "/tags",
        component: () => import('../views/tag/Tag'),
        meta: {
            title: "标签"
        }
    },
    {
        path: "/classification",
        component: () => import('../views/classification/classification'),
        meta: {
            title: "分类"
        }
    },
    {
        path: "/classification/:classId",
        component: () => import('../views/article/ArticleList')
    },
    {
        path: "/albums",
        component: () => import('../views/album/Album'),
        meta: {
            title: "相册"
        }
    },
    {
        path: "/albums/:albumId",
        component: () => import('../views/album/Photo')
    },
    {
        path: "/talks",
        component: () => import('../views/talk/Talk'),
        meta: {
            title: "说说"
        }
    },
    {
        path: "/talks/:talkId",
        component: () => import('../views/talk/TalkInfo'),
        meta: {
            title: "说说"
        }
    },
    {
        path: "/links",
        component: () => import('../views/link/Link'),
        meta: {
            title: "友链列表"
        }
    },
    {
        path: "/about",
        component: () => import('../views/about/About'),
        meta: {
            title: "关于我"
        }
    },
    {
        path: "/message",
        component: () => import('../views/message/Message'),
        meta: {
            title: "留言板"
        }
    },
    {
        path: "/tags/:tagId",
        component: () => import('../views/article/ArticleList')
    },
    {
        path: "/user",
        component: () => import('../views/user/User'),
        meta: {
            title: "个人中心"
        }
    },
    {
        path: "/qq/myback",
        component: () => import('../components/OauthLogin')
    },
    {
        path: "/weibo/myback",
        component: () => import('../components/OauthLogin')
    }
];

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes
});

export default router;
