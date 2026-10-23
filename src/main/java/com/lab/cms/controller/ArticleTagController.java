package com.lab.cms.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsArticleTag;
import com.lab.cms.service.CmsArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/articleTag")
public class ArticleTagController {
    @Autowired
    private CmsArticleTagService articleTagService;

    @GetMapping("/getByArticle")
    public Result<List<CmsArticleTag>> getByArticle(Integer articleId){
        LambdaQueryWrapper<CmsArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticleTag::getArticleId,articleId);
        return Result.ok(articleTagService.list(wrapper));
    }
    @PostMapping("/bind")
    public Result<Void> bind(CmsArticleTag articleTag){
        articleTagService.save(articleTag);
        return Result.ok();
    }
    @DeleteMapping("/unbind")
    public Result<Void> unbind(Integer articleId,Integer tagId){
        LambdaQueryWrapper<CmsArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsArticleTag::getArticleId,articleId).eq(CmsArticleTag::getTagId,tagId);
        articleTagService.remove(wrapper);
        return Result.ok();
    }
}