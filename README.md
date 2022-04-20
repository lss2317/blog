## 博客介绍

<p align=center>
  <a href="https://lsstop.com">
    <img src="https://blog-1307541812.cos.ap-shanghai.myqcloud.com/b1cd90ef-9bcf-406e-88f5-2beedbd116e0.jpeg" alt="阿圣的个人博客" style="border-radius: 50%">
  </a>
</p>


<p align=center>
   基于Springboot + Vue 开发的前后端分离博客
</p>

<p align="center">
   <a target="_blank" href="https://github.com/lss2317/blog">
      <img src="https://img.shields.io/hexpm/l/plug.svg"/>
      <img src="https://img.shields.io/badge/JDK-1.8+-green.svg"/>
      <img src="https://img.shields.io/badge/springboot-2.5.4.RELEASE-green"/>
      <img src="https://img.shields.io/badge/vue-2.6.11-green"/>
      <img src="https://img.shields.io/badge/mysql-8.0.25-green"/>
      <img src="https://img.shields.io/badge/mybatis--plus-3.5.1-green"/>
      <img src="https://img.shields.io/badge/redis-6.2.5-green"/>
      <img src="https://img.shields.io/badge/rabbitmq-3.8.11-green"/>
   </a>
</p>

  [在线地址](#在线地址) |[目录结构](#目录结构) |[项目特点](#项目特点) |[技术介绍](#技术介绍) |[运行环境](#运行环境) |[开发环境](#开发环境) |[项目截图](#项目截图)

## 在线地址

**项目链接：** [lsstop.com](https://lsstop.com)

**后台链接：** [admin.lsstop.com](https://admin.lsstop.com)

**测试账号**：test@qq.com，密码：123456，可登入后台查看。

**Github地址：** [https://github.com/lss2317/blog](https://github.com/lss2317/blog)

**Gitee地址：** [https://gitee.com/lss2084447140/blog](https://gitee.com/lss2084447140/blog)

## 目录结构

前端项目位于为vue-blog和vue-admin，blog为前台，admin为后台。

后端项目位于springboot-blog下。

SQL文件位于根目录下的**blog_lss.sql**，需要MYSQL8以上版本。

可直接导入该项目于本地，修改后端配置文件中的数据库等连接信息，项目中使用到的关于腾讯云功能和第三方授权登录等需要自行开通。

**ps：请先运行后端项目，再启动前端项目，前端项目配置由后端动态加载。** 

```
springboot-blog
├── annotation    --  自定义注解
├── aspect        --  aop模块
├── config        --  配置模块
├── constant      --  常量模块
├── consumer      --  MQ消费者模块
├── controller    --  控制器模块
├── entity        --  实体类模块
├── enums         --  枚举模块,反馈信息给前端
├── handler       --  处理器模块(Filter拦截器配置)
├── mapper        --  框架核心模块
├── service       --  服务模块
├── strategy      --  策略模块（第三方登录）
└── util          --  工具类模块
```

## 项目特点

- [x] 前台参考"Hexo"的"Butterfly"设计，美观简洁，响应式体验好。
- [x] 采用Markdown编辑器，写法简单。
- [x] 评论支持表情输入回复等，样式参考Valine。
- [x] 前后端分离部署，适应当前潮流。
- [x] 接入第三方登录，减少注册成本。
- [x] 支持发布说说，随时分享趣事。
- [x] 留言采用弹幕墙，更加炫酷。
- [x] 支持代码高亮和复制，图片预览，深色模式等功能，提升用户体验。
- [x] 新增文章目录、推荐文章等功能，优化用户体验。
- [x] 新增aop注解实现日志管理。  
- [x] 后台管理支持修改背景图片，博客配置等信息，操作简单，支持上传相册。
- [x] 评论和发布说说新增QQ表情包
- [x] L2Dwidget添加动漫人物
- [x] 搜索文章支持高亮分词，响应速度快。
- [x] 在线聊天室，支持撤回、语音输入、统计未读数量等功能。
- [x] 添加音乐播放器，支持在线搜索歌曲。

## 技术介绍

**前端：** vue + vuex + vue-router + axios + vuetify + element + echarts

**后端：** SpringBoot + nginx + MyBatisPlus + Mysql + Redis+ RabbitMQ 

**其他：** 接入QQ，微博第三方登录，接入腾讯云人机验证，文件腾讯云COS储存

## 开发环境

| 开发工具 | 说明              |
| -------- | ----------------- |
| IDEA     | Java开发工具IDE   |
| VSCode   | Vue开发工具IDE    |
| Navicat  | MySQL远程连接工具 |

| 开发环境 | 版本   |
| -------- | ------ |
| JDK      | 1.8    |
| MySQL    | 8.0.25 |
| Redis    | 6.2.5  |
| RabbitMQ | 3.8.11 |

## 项目截图

![home.png](https://www.staic.lsstop.com//introduce/blog-home.png)

![article.png](https://blog-1307541812.cos.ap-shanghai.myqcloud.com/article.png)

![admin.png](https://blog-1307541812.cos.ap-shanghai.myqcloud.com/admin-home.png)

![talk.png](https://blog-1307541812.cos.ap-shanghai.myqcloud.com/talk.png)
