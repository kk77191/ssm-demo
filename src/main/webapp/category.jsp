<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>栏目管理 - CMS</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{background:#f5f7fb;min-height:100vh}
.navbar{background:#fff!important;box-shadow:0 2px 10px rgba(0,0,0,.08)}
.navbar-brand{font-weight:700;font-size:20px;color:#667eea!important}
.page-title{font-size:24px;font-weight:700;color:#333;margin:25px 0 5px}
.page-subtitle{color:#888;font-size:14px;margin-bottom:25px}
.table-card{border:none;border-radius:12px;background:#fff;box-shadow:0 4px 20px rgba(0,0,0,.06);overflow:hidden}
.table thead th{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border:none;font-weight:600;font-size:14px;padding:15px}
.table tbody td{padding:14px 15px;vertical-align:middle;border-color:#f0f0f0;font-size:14px}
.table tbody tr:hover{background:#fafbff}
.badge-count{background:#eef0ff;color:#667eea;font-weight:600;border-radius:20px;padding:4px 12px;font-size:13px}
.back-btn{color:#667eea;text-decoration:none;font-weight:500}
.back-btn:hover{text-decoration:underline}
</style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light">
<div class="container">
<a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">📚 CMS 内容管理系统</a>
<div class="ms-auto">
<a href="${pageContext.request.contextPath}/index.jsp" class="back-btn">← 返回首页</a>
</div>
</div>
</nav>

<div class="container">
<h2 class="page-title">📂 栏目管理</h2>
<p class="page-subtitle">管理系统中的所有栏目分类</p>

<div class="table-card">
<table class="table table-hover mb-0">
<thead>
<tr>
<th style="width:80px">ID</th>
<th>栏目名称</th>
<th>描述</th>
</tr>
</thead>
<tbody>
<c:forEach items="${categories}" var="cat">
<tr>
<td><span class="badge-count">${cat.id}</span></td>
<td><strong>${cat.catName}</strong></td>
<td class="text-muted">${cat.catDesc}</td>
</tr>
</c:forEach>
</tbody>
</table>
</div>

<p class="text-muted mt-3" style="font-size:13px">共 ${categories.size()} 条记录</p>
</div>
</body>
</html>