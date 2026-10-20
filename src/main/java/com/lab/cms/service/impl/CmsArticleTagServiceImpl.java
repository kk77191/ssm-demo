package com.lab.cms.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.CmsArticleTag;
import com.lab.cms.mapper.CmsArticleTagMapper;
import com.lab.cms.service.CmsArticleTagService;
import org.springframework.stereotype.Service;
@Service
public class CmsArticleTagServiceImpl extends ServiceImpl<CmsArticleTagMapper, CmsArticleTag> implements CmsArticleTagService {
}