package com.lab.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class CmsTag {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String tagName;

    /** 逻辑删除标记，同 CmsArticle */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
