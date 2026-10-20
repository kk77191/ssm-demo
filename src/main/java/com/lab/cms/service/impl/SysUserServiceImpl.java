package com.lab.cms.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.SysUser;
import com.lab.cms.mapper.SysUserMapper;
import com.lab.cms.service.SysUserService;
import org.springframework.stereotype.Service;
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public SysUser login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username).eq(SysUser::getPassword,password);
        return baseMapper.selectOne(wrapper);
    }
}