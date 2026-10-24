package com.lab.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lab.cms.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
    boolean register(String username, String password);
    boolean assignRole(Integer userId, String role);
}