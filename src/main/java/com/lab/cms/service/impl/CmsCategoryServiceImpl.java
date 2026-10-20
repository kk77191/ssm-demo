package com.lab.cms.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.CmsCategory;
import com.lab.cms.mapper.CmsCategoryMapper;
import com.lab.cms.service.CmsCategoryService;
import org.springframework.stereotype.Service;
@Service
public class CmsCategoryServiceImpl extends ServiceImpl<CmsCategoryMapper, CmsCategory> implements CmsCategoryService {
}