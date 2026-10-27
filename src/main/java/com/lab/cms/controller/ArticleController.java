package com.lab.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lab.cms.annotation.Log;
import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.service.CmsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private CmsArticleService articleService;

    @GetMapping("/list")
    public Result<IPage<CmsArticle>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "5") Integer pageSize,
                                          Integer catId) {
        return Result.ok(articleService.search(pageNum, pageSize, null, catId, null));
    }

    @GetMapping("/search")
    public Result<IPage<CmsArticle>> search(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "5") Integer pageSize,
                                            String keyword,
                                            Integer catId,
                                            Integer status) {
        return Result.ok(articleService.search(pageNum, pageSize, keyword, catId, status));
    }

    @GetMapping("/{id}")
    public Result<CmsArticle> getById(@PathVariable Integer id) {
        return Result.ok(articleService.getById(id));
    }

    @RequireRole("admin")
    @Log("新增文章")
    @PostMapping("/add")
    public Result<Void> add(CmsArticle article) {
        articleService.save(article);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("编辑文章")
    @PutMapping("/update")
    public Result<Void> update(CmsArticle article) {
        articleService.updateById(article);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        articleService.removeById(id);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("批量发布文章")
    @PutMapping("/batchPublish")
    public Result<Void> batchPublish(@RequestBody List<Integer> ids) {
        articleService.batchPublish(ids);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("批量下架文章")
    @PutMapping("/batchOffShelf")
    public Result<Void> batchOffShelf(@RequestBody List<Integer> ids) {
        articleService.batchOffShelf(ids);
        return Result.ok();
    }

    @RequireRole("admin")
    @Log("批量删除文章")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        articleService.removeByIds(ids);
        return Result.ok();
    }
}