package com.lab.cms.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.service.CmsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private CmsArticleService articleService;

    @GetMapping("/list")
    public Result<IPage<CmsArticle>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "5") Integer pageSize,
                                          Integer catId){
        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<>();
        if(catId != null){
            wrapper.eq(CmsArticle::getCatId,catId);
        }
        return Result.ok(articleService.page(new Page<>(pageNum,pageSize),wrapper));
    }
    @PostMapping("/add")
    public Result<Void> add(CmsArticle article){
        articleService.save(article);
        return Result.ok();
    }
    @PutMapping("/update")
    public Result<Void> update(CmsArticle article){
        articleService.updateById(article);
        return Result.ok();
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id){
        articleService.removeById(id);
        return Result.ok();
    }
}