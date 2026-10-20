package com.lab.cms.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.mapper.CmsArticleMapper;
import com.lab.cms.service.CmsArticleService;
import org.springframework.stereotype.Service;
@Service
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements CmsArticleService {
}