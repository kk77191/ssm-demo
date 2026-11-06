package com.lab.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lab.cms.entity.CmsArticle;

import java.time.LocalDate;
import java.util.List;

public interface CmsArticleService extends IService<CmsArticle> {

    /**
     * 分页检索文章。
     *
     * @param pageNum   页码，从 1 开始
     * @param pageSize  每页条数
     * @param keyword   关键词（标题或正文模糊匹配）
     * @param catId     栏目 id
     * @param status    状态 0 草稿 / 1 已发布 / 2 已下架
     * @param author    作者（模糊匹配）
     * @param beginDate 创建时间起始日期，含当天 00:00:00
     * @param endDate   创建时间截止日期，含当天 23:59:59
     * @param sort      排序字段，支持多字段逗号分隔，如 "status,createTime"
     * @param order     排序方向，逗号分隔，与 sort 一一对应，如 "asc,desc"
     * @return 分页结果
     */
    IPage<CmsArticle> search(Integer pageNum, Integer pageSize, String keyword, Integer catId,
                             Integer status, String author, LocalDate beginDate, LocalDate endDate,
                             String sort, String order);

    boolean batchUpdateStatus(List<Integer> ids, Integer status);

    boolean batchPublish(List<Integer> ids);

    boolean batchOffShelf(List<Integer> ids);
}
