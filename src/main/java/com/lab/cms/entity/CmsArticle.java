package com.lab.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}