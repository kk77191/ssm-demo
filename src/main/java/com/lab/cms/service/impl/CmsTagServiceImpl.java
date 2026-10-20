package com.lab.cms.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.CmsTag;
import com.lab.cms.mapper.CmsTagMapper;
import com.lab.cms.service.CmsTagService;
import org.springframework.stereotype.Service;
@Service
public class CmsTagServiceImpl extends ServiceImpl<CmsTagMapper, CmsTag> implements CmsTagService {
}