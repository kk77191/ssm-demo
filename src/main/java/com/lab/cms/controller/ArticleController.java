package com.lab.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lab.cms.annotation.Log;
import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.PageResult;
import com.lab.cms.common.Result;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.service.CmsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 文章资源接口（RESTful 风格）。
 * <p>
 * 统一以 /api/articles 作为资源路径，用 HTTP 动词表达操作语义：
 * GET 查询、POST 新增、PUT 修改、DELETE 删除。
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private CmsArticleService articleService;

    /**
     * 分页查询文章列表，支持关键词、作者、栏目、状态、时间区间组合筛选与多字段动态排序。
     * <p>
     * GET /api/articles?pageNum=1&pageSize=10&keyword=xx&author=admin&categoryId=2&status=1
     * &amp;beginDate=2024-05-01&amp;endDate=2024-05-31&amp;sort=status,createTime&amp;order=asc,desc
     * <p>
     * sort 可选值（白名单）：id、title、author、status、createTime、updateTime，支持逗号分隔多字段；
     * order 与 sort 逐位对应，取 asc / desc，缺省为 desc；
     * beginDate / endDate 只填日期，服务端按「起始日 00:00:00 ~ 截止日 23:59:59」转化为时间区间。
     */
    @GetMapping
    public Result<PageResult<CmsArticle>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String author,
            // 前端 date 控件提交的是 yyyy-MM-dd，用 @DateTimeFormat 指定格式后可直接绑定为 LocalDate
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "desc") String order) {
        IPage<CmsArticle> page = articleService.search(pageNum, pageSize, keyword, categoryId, status,
                author, beginDate, endDate, sort, order);
        return Result.ok(PageResult.of(page));
    }

    /**
     * 查询单篇文章详情。
     * GET /api/articles/{id}
     */
    @GetMapping("/{id}")
    public Result<CmsArticle> getById(@PathVariable Integer id) {
        return Result.ok(articleService.getById(id));
    }

    /**
     * 新增文章。
     * POST /api/articles
     */
    @RequireRole("admin")
    @Log("新增文章")
    @PostMapping
    public Result<Void> add(CmsArticle article) {
        articleService.save(article);
        return Result.ok();
    }

    /**
     * 修改文章，主键从路径取，避免请求体里的 id 与路径不一致。
     * PUT /api/articles/{id}
     */
    @RequireRole("admin")
    @Log("编辑文章")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, CmsArticle article) {
        article.setId(id);
        articleService.updateById(article);
        return Result.ok();
    }

    /**
     * 删除单篇文章。
     * DELETE /api/articles/{id}
     */
    @RequireRole("admin")
    @Log("删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        articleService.removeById(id);
        return Result.ok();
    }

    /**
     * 批量发布。
     * PUT /api/articles/batch-publish
     */
    @RequireRole("admin")
    @Log("批量发布文章")
    @PutMapping("/batch-publish")
    public Result<Void> batchPublish(@RequestBody List<Integer> ids) {
        articleService.batchPublish(ids);
        return Result.ok();
    }

    /**
     * 批量下架。
     * PUT /api/articles/batch-off-shelf
     */
    @RequireRole("admin")
    @Log("批量下架文章")
    @PutMapping("/batch-off-shelf")
    public Result<Void> batchOffShelf(@RequestBody List<Integer> ids) {
        articleService.batchOffShelf(ids);
        return Result.ok();
    }

    /**
     * 批量删除。
     * DELETE /api/articles/batch
     */
    @RequireRole("admin")
    @Log("批量删除文章")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        articleService.removeByIds(ids);
        return Result.ok();
    }
}
