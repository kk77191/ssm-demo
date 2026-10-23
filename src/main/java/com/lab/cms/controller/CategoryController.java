package com.lab.cms.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsCategory;
import com.lab.cms.service.CmsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CmsCategoryService categoryService;

    @GetMapping("/list")
    public Result<IPage<CmsCategory>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "5") Integer pageSize){
        return Result.ok(categoryService.page(new Page<>(pageNum,pageSize)));
    }
    @PostMapping("/add")
    public Result<Void> add(CmsCategory category){
        categoryService.save(category);
        return Result.ok();
    }
    @PutMapping("/update")
    public Result<Void> update(CmsCategory category){
        categoryService.updateById(category);
        return Result.ok();
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id){
        categoryService.removeById(id);
        return Result.ok();
    }
}