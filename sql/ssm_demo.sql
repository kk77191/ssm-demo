CREATE DATABASE IF NOT EXISTS mvc 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_general_ci;

USE mvc;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(30) NOT NULL COMMENT '账号',
    password VARCHAR(30) NOT NULL COMMENT '密码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_user(username,password) VALUES
('admin','123456'),
('zhangsan','666666'),
('lisi','888888'),
('wangwu','abc123'),
('xiaoming','pass001'),
('xiaohong','pass002'),
('dabao','111111'),
('erbao','222222');

DROP TABLE IF EXISTS cms_category;
CREATE TABLE cms_category (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '栏目id',
    cat_name VARCHAR(50) NOT NULL COMMENT '栏目名称',
    cat_desc VARCHAR(100) COMMENT '栏目描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cms_category(cat_name,cat_desc) VALUES
('技术分享','技术文章栏目'),
('生活随笔','日常随笔栏目'),
('前端开发','前端技术文章'),
('后端架构','后端架构设计'),
('数据库','数据库技术'),
('移动开发','移动端开发'),
('开源项目','开源项目推荐'),
('求职面试','面试经验分享');

DROP TABLE IF EXISTS cms_tag;
CREATE TABLE cms_tag (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '标签id',
    tag_name VARCHAR(30) NOT NULL COMMENT '标签名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cms_tag(tag_name) VALUES
('Java'),('SSM'),('MySQL'),('Redis'),('SpringBoot'),
('Vue'),('React'),('前端'),('后端'),('微服务'),
('Docker'),('JVM'),('算法'),('面试'),('开源'),
('Kotlin'),('HTML5'),('Git'),('Linux'),('设计模式');

DROP TABLE IF EXISTS cms_article;
CREATE TABLE cms_article (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '文章id',
    title VARCHAR(100) NOT NULL COMMENT '文章标题',
    content TEXT COMMENT '文章内容',
    cat_id INT COMMENT '所属栏目id',
    FOREIGN KEY (cat_id) REFERENCES cms_category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cms_article(title,content,cat_id) VALUES
('SSM整合教程','详细介绍Spring+SpringMVC+MyBatis三大框架整合步骤及注意事项',1),
('SpringBoot快速入门','SpringBoot自动配置原理详解，开箱即用的开发体验',1),
('Vue3组合式API','Vue3 Composition API 与 Options API 对比分析',3),
('微服务架构设计','SpringCloud微服务架构设计模式与实战',4),
('MySQL索引优化','MySQL B+树索引原理及SQL优化技巧',5),
('Redis缓存实战','Redis五种数据结构及缓存策略实践',5),
('Kotlin入门指南','Kotlin语法特性与Java互操作详解',6),
('Docker容器化部署','Docker常用命令及Java应用容器化打包部署',4),
('React Hooks精讲','useState useEffect useContext useReducer 全解析',3),
('MyBatis Plus实战','MyBatis Plus常用CRUD及分页插件使用',1),
('JVM调优实战','JVM内存模型GC算法及调优案例分析',4),
('周末爬山记','周末和朋友去香山爬山，风景优美空气清新',2),
('读书笔记-深入理解计算机系统','CSAPP这本书真的非常经典，推荐大家阅读',2),
('开源项目-RuoYi','基于SpringBoot的后台管理系统，代码结构清晰适合学习',7),
('开源项目-ELAdmin','ELAdmin前后端分离管理系统，前端Vue后端SpringBoot',7),
('面试经验分享-Java篇','整理了Java后端面试常见问题及答案',8),
('面试经验分享-算法篇','大厂面试算法题汇总，含解题思路和代码',8),
('Spring事务管理详解','Spring声明式事务传播机制及失效场景分析',1),
('HTML5新特性汇总','HTML5新增的语义化标签、Canvas、WebSocket等特性',3),
('Git工作流最佳实践','Git Flow GitHub Flow 及团队协作规范',4);

DROP TABLE IF EXISTS cms_article_tag;
CREATE TABLE cms_article_tag (
    article_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (article_id,tag_id),
    FOREIGN KEY (article_id) REFERENCES cms_article(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES cms_tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cms_article_tag(article_id,tag_id) VALUES
(1,1),(1,2),(1,3),
(2,1),(2,5),(2,10),
(3,6),(3,8),(3,17),
(4,4),(4,5),(4,10),(4,11),
(5,3),(5,20),
(6,3),(6,4),
(7,16),(7,1),
(8,11),(8,5),(8,1),
(9,7),(9,8),
(10,1),(10,2),
(11,1),(11,12),
(12,18),
(13,18),
(14,15),(14,1),(14,5),
(15,15),(15,5),
(16,14),(16,1),
(17,13),(17,14),
(18,1),(18,5),
(19,17),(19,8),
(20,18),(20,11);