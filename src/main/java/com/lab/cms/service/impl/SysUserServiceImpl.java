package com.lab.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.common.BusinessException;
import com.lab.cms.entity.SysUser;
import com.lab.cms.mapper.SysUserMapper;
import com.lab.cms.service.SysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username).eq(SysUser::getPassword, password);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("账号不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException("密码至少6位");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("账号已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        return baseMapper.insert(user) > 0;
    }

    @Override
    public boolean assignRole(Integer userId, String role) {
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setRole(role);
        return baseMapper.updateById(user) > 0;
    }
}