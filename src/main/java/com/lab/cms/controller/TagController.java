package com.lab.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lab.cms.annotation.Log;
import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsTag;
import com.lab.cms.service.CmsTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private CmsTagService tagService;

    @GetMapping("/list")
    public Result<IPage<CmsTag>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "5") Integer pageSize) {
        return Result.ok(tagService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/all")
    public Result<java.util.List<CmsTag>> all() {
        return Result.ok(tagService.list());
    }

    @GetMapping("/{id}")
    public Result<CmsTag> getById(@PathVariable Integer id) {
        return Result.ok(tagService.getById(id));
    }

    @RequireRole("admin")
    @Log("新增标签")
    @PostMapping("/add")
    public Result<Void> add(CmsTag tag) {
        tagService.save(tag);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("编辑标签")
    @PutMapping("/update")
    public Result<Void> update(CmsTag tag) {
        tagService.updateById(tag);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        tagService.removeById(id);
        return Result.ok();
    }
}