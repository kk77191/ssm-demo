package com.lab.cms.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
                                      @RequestParam(defaultValue = "5") Integer pageSize){
        return Result.ok(tagService.page(new Page<>(pageNum,pageSize)));
    }
    @PostMapping("/add")
    public Result<Void> add(CmsTag tag){
        tagService.save(tag);
        return Result.ok();
    }
    @PutMapping("/update")
    public Result<Void> update(CmsTag tag){
        tagService.updateById(tag);
        return Result.ok();
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id){
        tagService.removeById(id);
        return Result.ok();
    }
}