package com.lab.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.common.BusinessException;
import com.lab.cms.entity.CmsCategory;
import com.lab.cms.mapper.CmsCategoryMapper;
import com.lab.cms.service.CmsCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CmsCategoryServiceImpl extends ServiceImpl<CmsCategoryMapper, CmsCategory> implements CmsCategoryService {

    @Override
    public List<CmsCategory> tree() {
        List<CmsCategory> all = baseMapper.selectList(
                new LambdaQueryWrapper<CmsCategory>().orderByAsc(CmsCategory::getParentId).orderByAsc(CmsCategory::getSort)
        );
        if (all.isEmpty()) {
            return all;
        }
        Map<Integer, List<CmsCategory>> groupByParent = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0 : c.getParentId()));
        List<CmsCategory> result = new ArrayList<>();
        buildTree(0, groupByParent, result);
        return result;
    }

    private void buildTree(Integer parentId, Map<Integer, List<CmsCategory>> map, List<CmsCategory> result) {
        List<CmsCategory> children = map.get(parentId);
        if (children == null) return;
        result.addAll(children);
        for (CmsCategory c : children) {
            buildTree(c.getId(), map, result);
        }
    }

    @Override
    public boolean toggleStatus(Integer id) {
        CmsCategory cat = baseMapper.selectById(id);
        if (cat == null) {
            throw new BusinessException("分类不存在");
        }
        cat.setStatus(cat.getStatus() == 1 ? 0 : 1);
        return baseMapper.updateById(cat) > 0;
    }

    @Override
    public boolean move(Integer id, Integer parentId, Integer sort) {
        CmsCategory cat = baseMapper.selectById(id);
        if (cat == null) {
            throw new BusinessException("分类不存在");
        }
        if (parentId != null && parentId.equals(id)) {
            throw new BusinessException("不能把自己设为父分类");
        }
        if (parentId != null && parentId > 0) {
            CmsCategory parent = baseMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }
        cat.setParentId(parentId);
        cat.setSort(sort);
        return baseMapper.updateById(cat) > 0;
    }
}