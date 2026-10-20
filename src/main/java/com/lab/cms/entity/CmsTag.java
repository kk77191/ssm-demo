package com.lab.cms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
@Data
public class CmsTag {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String tagName;
}