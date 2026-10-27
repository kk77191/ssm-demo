package com.lab.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lab.cms.entity.CmsArticle;

import java.util.List;

public interface CmsArticleService extends IService<CmsArticle> {
    IPage<CmsArticle> search(Integer pageNum, Integer pageSize, String keyword, Integer catId, Integer status);
    boolean batchUpdateStatus(List<Integer> ids, Integer status);
    boolean batchPublish(List<Integer> ids);
    boolean batchOffShelf(List<Integer> ids);
}