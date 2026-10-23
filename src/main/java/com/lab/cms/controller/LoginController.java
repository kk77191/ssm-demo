package com.lab.cms.controller;
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

    @PostMapping
    public Result<Void> login(String username, String password, HttpSession session) {
        SysUser user = userService.login(username, password);
        if (user == null) {
            return Result.fail("账号密码错误");
        }
        session.setAttribute("loginUser", user);
        return Result.ok();
    }

    @GetMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
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