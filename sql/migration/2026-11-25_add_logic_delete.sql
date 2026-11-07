-- =============================================================
-- 迁移脚本：为内容表增加逻辑删除标记
-- 执行时间：2026-11-25
-- 说明：
--   配合 MyBatis-Plus @TableLogic，DELETE 操作不再物理删除行，
--   而是把 deleted 置为 1，查询时自动追加 deleted = 0 条件。
--   这样误删的数据仍可追溯、可恢复，符合内容管理类系统的常见要求。
--
-- 回滚：
--   ALTER TABLE cms_article  DROP COLUMN deleted;
--   ALTER TABLE cms_category DROP COLUMN deleted;
--   ALTER TABLE cms_tag      DROP COLUMN deleted;
-- =============================================================

USE mvc;

ALTER TABLE cms_article
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1已删除' AFTER update_time;

ALTER TABLE cms_category
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1已删除' AFTER status;

ALTER TABLE cms_tag
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1已删除' AFTER tag_name;

-- 建议：逻辑删除后查询条件会固定带上 deleted = 0，为它建索引有助于大表过滤
CREATE INDEX idx_article_deleted ON cms_article (deleted);
