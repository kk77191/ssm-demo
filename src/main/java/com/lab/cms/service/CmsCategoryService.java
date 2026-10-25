package com.lab.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lab.cms.entity.CmsCategory;

import java.util.List;

public interface CmsCategoryService extends IService<CmsCategory> {
    List<CmsCategory> tree();
    boolean toggleStatus(Integer id);
    boolean move(Integer id, Integer parentId, Integer sort);
}