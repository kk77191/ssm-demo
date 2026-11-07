package com.lab.cms.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充处理器。
 * <p>
 * 场景：几乎每张表都有「创建时间 / 更新时间」，如果每个 Service 都手动 set 一遍，
 * 代码重复且容易漏（例如批量操作时忘记更新 updateTime）。
 * <p>
 * 做法：实体字段标注 {@code @TableField(fill = ...)}，再由本处理器在
 * 执行 INSERT / UPDATE 前统一填值。业务代码里不需要出现任何一句 setCreateTime。
 * <p>
 * 这里用 strictXxxFill 而不是 setFieldValByName：strict 版本会先校验实体上
 * 确实存在该字段且声明了填充策略，字段不存在时静默跳过，不会误写；
 * 同时它只在字段值为 null 时填充，调用方显式赋的值不会被覆盖。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
