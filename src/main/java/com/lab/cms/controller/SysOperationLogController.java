package com.lab.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.Result;
import com.lab.cms.entity.SysOperationLog;
import com.lab.cms.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
@RequireRole("admin")
public class SysOperationLogController {
    @Autowired
    private SysOperationLogService operationLogService;

    @GetMapping("/list")
    public Result<IPage<SysOperationLog>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(operationLogService.page(new Page<>(pageNum, pageSize)));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operationLogService.removeById(id);
        return Result.ok();
    }

    @DeleteMapping("/clear")
    public Result<Void> clear() {
        operationLogService.remove(null);
        return Result.ok();
    }
}