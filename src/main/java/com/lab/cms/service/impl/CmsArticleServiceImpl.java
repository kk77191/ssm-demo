package com.lab.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.mapper.CmsArticleMapper;
import com.lab.cms.service.CmsArticleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements CmsArticleService {

    @Override
    public IPage<CmsArticle> search(Integer pageNum, Integer pageSize, String keyword, Integer catId, Integer status) {
        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(CmsArticle::getTitle, keyword.trim())
                    .or().like(CmsArticle::getContent, keyword.trim()));
        }
        if (catId != null) {
            wrapper.eq(CmsArticle::getCatId, catId);
        }
        if (status != null) {
            wrapper.eq(CmsArticle::getStatus, status);
        }
        wrapper.orderByDesc(CmsArticle::getId);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean batchUpdateStatus(List<Integer> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        LambdaUpdateWrapper<CmsArticle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(CmsArticle::getId, ids)
                .set(CmsArticle::getStatus, status)
                .set(CmsArticle::getUpdateTime, LocalDateTime.now());
        return baseMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean batchPublish(List<Integer> ids) {
        return batchUpdateStatus(ids, 1);
    }

    @Override
    public boolean batchOffShelf(List<Integer> ids) {
        return batchUpdateStatus(ids, 2);
    }
}