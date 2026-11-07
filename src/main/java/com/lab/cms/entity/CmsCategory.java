package com.lab.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class CmsCategory {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String catName;

    private String catDesc;

    private Integer parentId;

    private Integer sort;

    private Integer status;

    /** 逻辑删除标记，同 CmsArticle */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
