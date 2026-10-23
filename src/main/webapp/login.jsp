<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>CMS 登录</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);min-height:100vh;display:flex;align-items:center;justify-content:center}
.login-card{border:none;border-radius:16px;box-shadow:0 20px 60px rgba(0,0,0,.3);width:400px}
.login-card .card-body{padding:40px}
.login-card h2{color:#333;font-weight:700;margin-bottom:30px;text-align:center}
.form-control{border-radius:8px;padding:12px;font-size:15px;border:2px solid #eee;transition:all .2s}
.form-control:focus{border-color:#667eea;box-shadow:0 0 0 3px rgba(102,126,234,.2)}
.btn-login{width:100%;padding:12px;border-radius:8px;font-size:16px;font-weight:600;background:linear-gradient(135deg,#667eea,#764ba2);border:none;box-shadow:0 4px 15px rgba(102,126,234,.4);transition:transform .2s}
.btn-login:hover{transform:translateY(-2px);box-shadow:0 6px 20px rgba(102,126,234,.5)}
</style>
</head>
<body>
<div class="card login-card">
<div class="card-body">
<h2>CMS 登录</h2>
<% if ("1".equals(request.getParameter("error"))) { %>
    <div class="alert alert-danger text-center" role="alert">账号或密码错误，请重试</div>
<% } %>
<form action="${pageContext.request.contextPath}/login/doLogin" method="post">
<div class="mb-3">
<label class="form-label">账号</label>
<input type="text" name="username" class="form-control" placeholder="请输入账号" required>
</div>
<div class="mb-4">
<label class="form-label">密码</label>
<input type="password" name="password" class="form-control" placeholder="请输入密码" required>
</div>
<button type="submit" class="btn btn-login text-white">登 录</button>
</form>
</div>
</div>
</body>
</html>