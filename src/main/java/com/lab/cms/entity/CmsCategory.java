package com.lab.cms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
@Data
public class CmsCategory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String catName;
    private String catDesc;
}