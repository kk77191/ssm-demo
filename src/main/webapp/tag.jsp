<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>标签管理 - CMS</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{background:#f5f7fb;min-height:100vh}
.navbar{background:#fff!important;box-shadow:0 2px 10px rgba(0,0,0,.08)}
.navbar-brand{font-weight:700;font-size:20px;color:#667eea!important}
.page-title{font-size:24px;font-weight:700;color:#333;margin:25px 0 5px}
.page-subtitle{color:#888;font-size:14px;margin-bottom:25px}
.table-card{border:none;border-radius:12px;background:#fff;box-shadow:0 4px 20px rgba(0,0,0,.06);overflow:hidden}
.table thead th{background:linear-gradient(135deg,#4facfe,#00f2fe);color:#fff;border:none;font-weight:600;font-size:14px;padding:15px}
.table tbody td{padding:14px 15px;vertical-align:middle;border-color:#f0f0f0;font-size:14px}
.table tbody tr:hover{background:#f0fbff}
.tag-chip{display:inline-block;background:#eefaff;color:#0ea5e9;border-radius:20px;padding:6px 16px;font-size:14px;font-weight:500}
.back-btn{color:#667eea;text-decoration:none;font-weight:500}
.back-btn:hover{text-decoration:underline}
.btn-primary{background:linear-gradient(135deg,#4facfe,#00f2fe);border:none;font-weight:500}
.btn-primary:hover{opacity:.9}
.action-bar{background:#fff;border-radius:12px;padding:15px 20px;box-shadow:0 4px 20px rgba(0,0,0,.06);margin-bottom:20px;display:flex;align-items:center;gap:15px}
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
<h2 class="page-title">🏷️ 标签管理</h2>
<p class="page-subtitle">文章标签的新增、编辑、删除</p>

<div class="action-bar">
<button class="btn btn-primary" onclick="openAddModal()">+ 新增标签</button>
<button class="btn btn-outline-secondary btn-sm" onclick="refresh()">🔄 刷新</button>
<span class="text-muted ms-auto" style="font-size:13px" id="totalInfo">—</span>
</div>

<div class="table-card">
<table class="table table-hover mb-0">
<thead>
<tr>
<th style="width:70px">ID</th>
<th>标签名称</th>
<th style="width:280px">操作</th>
</tr>
</thead>
<tbody id="tbody"></tbody>
</table>
</div>
</div>

<div class="modal fade" id="editModal" tabindex="-1">
<div class="modal-dialog modal-dialog-centered">
<div class="modal-content">
<div class="modal-header" style="background:linear-gradient(135deg,#4facfe,#00f2fe);color:#fff;border:none">
<h5 class="modal-title" id="modalTitle">新增标签</h5>
<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
</div>
<div class="modal-body p-4">
<input type="hidden" id="tagId">
<div class="mb-3">
<label class="form-label fw-bold">标签名称</label>
<input type="text" id="tagName" class="form-control" placeholder="如：Spring" required>
</div>
</div>
<div class="modal-footer">
<button type="button" class="btn btn-light" data-bs-dismiss="modal">取消</button>
<button type="button" class="btn btn-primary" onclick="save()">保存</button>
</div>
</div>
</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
const ctx = '${pageContext.request.contextPath}';
let modal;
window.addEventListener('DOMContentLoaded', () => { modal = new bootstrap.Modal(document.getElementById('editModal')); refresh(); });

function refresh() {
  fetch(ctx + '/tag/all').then(r => r.json()).then(res => {
    const tb = document.getElementById('tbody');
    tb.innerHTML = '';
    (res.data || []).forEach(t => {
      const tr = document.createElement('tr');
      tr.innerHTML =
        '<td class="text-muted">' + t.id + '</td>' +
        '<td><span class="tag-chip">🏷 # ' + t.tagName + '</span></td>' +
        '<td>' +
          '<button class="btn btn-sm btn-outline-secondary me-1" onclick="openEditModal(' + t.id + ')">编辑</button>' +
          '<button class="btn btn-sm btn-outline-danger" onclick="del(' + t.id + ')">删除</button>' +
        '</td>';
      tb.appendChild(tr);
    });
    document.getElementById('totalInfo').textContent = '共 ' + (res.data || []).length + ' 个标签';
  });
}

function openAddModal() {
  document.getElementById('modalTitle').textContent = '新增标签';
  document.getElementById('tagId').value = '';
  document.getElementById('tagName').value = '';
  modal.show();
}

function openEditModal(id) {
  fetch(ctx + '/tag/' + id).then(r => r.json()).then(res => {
    document.getElementById('modalTitle').textContent = '编辑标签';
    document.getElementById('tagId').value = res.data.id;
    document.getElementById('tagName').value = res.data.tagName;
    modal.show();
  });
}

function save() {
  const id = document.getElementById('tagId').value;
  const name = document.getElementById('tagName').value.trim();
  if (!name) { alert('请输入标签名称'); return; }
  const data = { tagName: name };
  if (id) data.id = parseInt(id);

  const url = id ? ctx + '/tag/update' : ctx + '/tag/add';
  fetch(url, {
    method: id ? 'PUT' : 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: new URLSearchParams(data).toString()
  }).then(r => r.json()).then(res => {
    if (res.code === 200) { modal.hide(); refresh(); }
    else alert(res.msg || '操作失败');
  });
}

function del(id) {
  if (!confirm('确定删除此标签？')) return;
  fetch(ctx + '/tag/' + id, {method: 'DELETE'}).then(r => r.json()).then(res => {
    if (res.code === 200) refresh(); else alert(res.msg);
  });
}
</script>
</body>
</html>