package com.lab.cms.controller;

import com.lab.cms.annotation.Log;
import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.Result;
import com.lab.cms.entity.SysUser;
import com.lab.cms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private SysUserService userService;

    @Log("用户登录")
    @PostMapping
    public Result<Void> login(String username, String password, HttpSession session) {
        SysUser user = userService.login(username, password);
        if (user == null) {
            return Result.fail("账号密码错误");
        }
        session.setAttribute("loginUser", user);
        return Result.ok();
    }

    @Log("用户注册")
    @PostMapping("/register")
    public Result<Void> register(String username, String password) {
        userService.register(username, password);
        return Result.ok();
    }

    @Log("用户退出")
    @GetMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<SysUser> info(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            return Result.fail("未登录");
        }
        SysUser vo = new SysUser();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());
        return Result.ok(vo);
    }

    @RequireRole("admin")
    @Log("分配角色")
    @PutMapping("/assignRole")
    public Result<Void> assignRole(Integer userId, String role) {
        userService.assignRole(userId, role);
        return Result.ok();
    }

    @PostMapping("/doLogin")
    public void doLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SysUser user = userService.login(username, password);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
            return;
        }
        request.getSession().setAttribute("loginUser", user);
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    @GetMapping("/doLogout")
    public void doLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}