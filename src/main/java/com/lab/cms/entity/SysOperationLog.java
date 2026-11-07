package com.lab.cms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ip;

    /** 日志时间由 MetaObjectHandler 统一填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}