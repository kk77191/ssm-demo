package com.lab.cms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CmsArticle {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String author;

    private String content;

    private Integer catId;

    private Integer status;

    /** 插入时由 MetaObjectHandler 自动填充，业务代码无需再 set */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 插入与更新时都自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0 正常 / 1 已删除。
     * 标注 @TableLogic 后，MyBatis-Plus 会自动在查询语句上追加 deleted = 0 条件，
     * 并把 removeById / removeByIds 改写成 UPDATE ... SET deleted = 1。
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
