<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>CMS 首页</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{background:#f5f7fb;min-height:100vh}
.navbar{background:#fff!important;box-shadow:0 2px 10px rgba(0,0,0,.08)}
.navbar-brand{font-weight:700;font-size:20px;color:#667eea!important}
.nav-link{color:#555!important;font-weight:500;margin:0 8px;border-radius:8px;transition:all .2s}
.nav-link:hover{background:#f0f2ff;color:#667eea!important}
.welcome-card{border:none;border-radius:16px;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;padding:40px;box-shadow:0 10px 40px rgba(102,126,234,.3);margin-top:30px}
.welcome-card h1{font-size:28px;font-weight:700;margin-bottom:10px}
.welcome-card p{opacity:.9;font-size:15px}
.menu-card{border:none;border-radius:12px;background:#fff;padding:25px;text-align:center;box-shadow:0 4px 20px rgba(0,0,0,.06);transition:all .3s;margin:15px 0}
.menu-card:hover{transform:translateY(-5px);box-shadow:0 10px 30px rgba(102,126,234,.15)}
.menu-icon{font-size:40px;margin-bottom:12px}
.menu-title{font-size:16px;font-weight:600;color:#333;text-decoration:none;display:block}
.menu-title:hover{color:#667eea}
</style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light">
<div class="container">
<a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">📚 CMS 内容管理系统</a>
<div class="ms-auto d-flex align-items-center">
<span class="text-muted me-3">欢迎 ${loginUser.username}</span>
<a href="${pageContext.request.contextPath}/login/doLogout" class="btn btn-outline-danger btn-sm">退出登录</a>
</div>
</div>
</nav>

<div class="container">
<div class="welcome-card">
<h1>你好，${loginUser.username} 👋</h1>
<p>欢迎使用 CMS 内容管理系统，请从下方选择要管理的模块</p>
</div>

<div class="row mt-4">
<div class="col-md-4">
<a href="${pageContext.request.contextPath}/category" class="menu-card">
<div class="menu-icon">📂</div>
<span class="menu-title">栏目管理</span>
</a>
</div>
<div class="col-md-4">
<a href="${pageContext.request.contextPath}/article" class="menu-card">
<div class="menu-icon">📝</div>
<span class="menu-title">文章管理</span>
</a>
</div>
<div class="col-md-4">
<a href="${pageContext.request.contextPath}/tag" class="menu-card">
<div class="menu-icon">🏷️</div>
<span class="menu-title">标签管理</span>
</a>
</div>
</div>
</div>
</body>
</html>