package com.lab.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lab.cms.annotation.Log;
import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsCategory;
import com.lab.cms.service.CmsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CmsCategoryService categoryService;

    @GetMapping("/list")
    public Result<IPage<CmsCategory>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(categoryService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/tree")
    public Result<List<CmsCategory>> tree() {
        return Result.ok(categoryService.tree());
    }

    @GetMapping("/{id}")
    public Result<CmsCategory> getById(@PathVariable Integer id) {
        return Result.ok(categoryService.getById(id));
    }

    @RequireRole("admin")
    @Log("新增分类")
    @PostMapping("/add")
    public Result<Void> add(CmsCategory category) {
        if (category.getParentId() == null) category.setParentId(0);
        if (category.getSort() == null) category.setSort(0);
        if (category.getStatus() == null) category.setStatus(1);
        categoryService.save(category);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("编辑分类")
    @PutMapping("/update")
    public Result<Void> update(CmsCategory category) {
        categoryService.updateById(category);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        categoryService.removeById(id);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("切换分类状态")
    @PutMapping("/toggle/{id}")
    public Result<Void> toggle(@PathVariable Integer id) {
        categoryService.toggleStatus(id);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("移动分类")
    @PutMapping("/move")
    public Result<Void> move(Integer id, Integer parentId, Integer sort) {
        categoryService.move(id, parentId, sort);
        return Result.ok();
    }
}