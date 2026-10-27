SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE mvc;
USE mvc;

DROP TABLE IF EXISTS sys_operation_log;
DROP TABLE IF EXISTS cms_article_tag;
DROP TABLE IF EXISTS cms_article;
DROP TABLE IF EXISTS cms_category;
DROP TABLE IF EXISTS cms_tag;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;
CREATE TABLE sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(30) NOT NULL COMMENT '账号',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: admin/user',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_user(username,password,role) VALUES
('admin','123456','admin'),
('zhangsan','666666','user'),
('lisi','888888','user'),
('wangwu','abc123','user'),
('xiaoming','pass001','user'),
('xiaohong','pass002','user'),
('dabao','111111','user'),
('erbao','222222','user');

DROP TABLE IF EXISTS cms_category;
CREATE TABLE cms_category (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '栏目id',
    cat_name VARCHAR(50) NOT NULL COMMENT '栏目名称',
    cat_desc VARCHAR(100) COMMENT '栏目描述',
    parent_id INT DEFAULT 0 COMMENT '父栏目id, 0为顶级',
    sort INT DEFAULT 0 COMMENT '排序, 数值越小越靠前',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cms_category(cat_name,cat_desc,parent_id,sort,status) VALUES
('技术分享','技术文章栏目',0,1,1),
('生活随笔','日常随笔栏目',0,2,1),
('前端开发','前端技术文章',1,1,1),
('后端架构','后端架构设计',1,2,1),
('数据库','数据库技术',1,3,1),
('移动开发','移动端开发',1,4,1),
('开源项目','开源项目推荐',0,3,1),
('求职面试','面试经验分享',0,4,1);

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
    author VARCHAR(50) DEFAULT 'admin' COMMENT '作者',
    content TEXT COMMENT '文章内容',
    cat_id INT COMMENT '所属栏目id',
    status TINYINT DEFAULT 0 COMMENT '状态: 0草稿 1已发布 2已下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (cat_id) REFERENCES cms_category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cms_article(title,content,cat_id,status) VALUES
('SSM整合教程','详细介绍Spring+SpringMVC+MyBatis三大框架整合步骤及注意事项',1,1),
('SpringBoot快速入门','SpringBoot自动配置原理详解，开箱即用的开发体验',1,1),
('Vue3组合式API','Vue3 Composition API 与 Options API 对比分析',3,1),
('微服务架构设计','SpringCloud微服务架构设计模式与实战',4,1),
('MySQL索引优化','MySQL B+树索引原理及SQL优化技巧',5,1),
('Redis缓存实战','Redis五种数据结构及缓存策略实践',5,1),
('Kotlin入门指南','Kotlin语法特性与Java互操作详解',6,2),
('Docker容器化部署','Docker常用命令及Java应用容器化打包部署',4,1),
('React Hooks精讲','useState useEffect useContext useReducer 全解析',3,1),
('MyBatis Plus实战','MyBatis Plus常用CRUD及分页插件使用',1,1),
('JVM调优实战','JVM内存模型GC算法及调优案例分析',4,0),
('周末爬山记','周末和朋友去香山爬山，风景优美空气清新',2,1),
('读书笔记-深入理解计算机系统','CSAPP这本书真的非常经典，推荐大家阅读',2,1),
('开源项目-RuoYi','基于SpringBoot的后台管理系统，代码结构清晰适合学习',7,1),
('开源项目-ELAdmin','ELAdmin前后端分离管理系统，前端Vue后端SpringBoot',7,1),
('面试经验分享-Java篇','整理了Java后端面试常见问题及答案',8,1),
('面试经验分享-算法篇','大厂面试算法题汇总，含解题思路和代码',8,0),
('Spring事务管理详解','Spring声明式事务传播机制及失效场景分析',1,1),
('HTML5新特性汇总','HTML5新增的语义化标签、Canvas、WebSocket等特性',3,2),
('Git工作流最佳实践','Git Flow GitHub Flow 及团队协作规范',4,1);

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

DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志id',
    user_id INT COMMENT '操作人id',
    username VARCHAR(30) COMMENT '操作人账号',
    operation VARCHAR(100) COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT '操作IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;