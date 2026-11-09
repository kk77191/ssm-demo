-- =============================================================
-- 迁移脚本：为列表页的高频查询条件补充索引
-- 执行时间：2026-11-26
--
-- 背景（优化前的执行计划）：
--   SELECT ... FROM cms_article WHERE deleted=0 AND status=1 ORDER BY create_time DESC
--   → type=ALL, key=NULL, Extra=Using filesort     即全表扫描 + 额外排序
--
-- 设计思路：
--   deleted 是逻辑删除后所有查询的固定条件，所以把它放在联合索引最左列；
--   status 是等值条件，create_time 是排序字段，按 (deleted, status, create_time) 组织，
--   可以同时满足"过滤 + 排序"，消除 filesort。
--
-- 回滚：
--   DROP INDEX idx_article_list      ON cms_article;
--   DROP INDEX idx_article_create    ON cms_article;
--   DROP INDEX idx_article_author    ON cms_article;
--   DROP INDEX idx_article_tag_tag   ON cms_article_tag;
--   DROP INDEX idx_category_parent   ON cms_category;
-- =============================================================

USE mvc;

-- 列表页组合筛选 + 按创建时间排序（最常用的路径）
CREATE INDEX idx_article_list ON cms_article (deleted, status, create_time);

-- 只按时间区间筛选（不带状态）时使用
CREATE INDEX idx_article_create ON cms_article (create_time);

-- 作者前缀检索（LIKE 'admin%' 可走索引；LIKE '%admin%' 走不了，见文档说明）
CREATE INDEX idx_article_author ON cms_article (author);

-- 标签反查文章：关联表已有 (article_id, tag_id) 主键，反向查询需要单独索引
CREATE INDEX idx_article_tag_tag ON cms_article_tag (tag_id);

-- 栏目树按父节点取子节点
CREATE INDEX idx_category_parent ON cms_category (parent_id);
