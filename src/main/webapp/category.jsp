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
.btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);border:none;font-weight:500}
.btn-primary:hover{opacity:.9}
.status-on{background:#dcfce7;color:#16a34a;border-radius:20px;padding:4px 12px;font-size:12px;font-weight:600}
.status-off{background:#fee2e2;color:#dc2626;border-radius:20px;padding:4px 12px;font-size:12px;font-weight:600}
.tree-indent{display:inline-block;width:20px}
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
<h2 class="page-title">📂 栏目管理</h2>
<p class="page-subtitle">多级分类的新增、编辑、排序、状态启停</p>

<div class="action-bar">
<button class="btn btn-primary" onclick="openAddModal(null)">+ 新增根栏目</button>
<button class="btn btn-outline-secondary btn-sm" onclick="refresh()">🔄 刷新树形</button>
<span class="text-muted ms-auto" style="font-size:13px" id="totalInfo">—</span>
</div>

<div class="table-card">
<table class="table table-hover mb-0">
<thead>
<tr>
<th style="width:70px">ID</th>
<th>栏目名称</th>
<th style="width:100px">父级</th>
<th style="width:80px">排序</th>
<th style="width:90px">状态</th>
<th>描述</th>
<th style="width:280px">操作</th>
</tr>
</thead>
<tbody id="tbody">
</tbody>
</table>
</div>
</div>

<!-- 新增/编辑 模态框 -->
<div class="modal fade" id="editModal" tabindex="-1">
<div class="modal-dialog modal-dialog-centered">
<div class="modal-content">
<div class="modal-header" style="background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border:none">
<h5 class="modal-title" id="modalTitle">新增栏目</h5>
<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
</div>
<div class="modal-body p-4">
<input type="hidden" id="catId">
<input type="hidden" id="catParentId">
<div class="mb-3">
<label class="form-label fw-bold">栏目名称</label>
<input type="text" id="catName" class="form-control" placeholder="如：技术文章" required>
</div>
<div class="mb-3">
<label class="form-label fw-bold">父级栏目</label>
<select id="parentSelect" class="form-select">
<option value="0">— 根栏目 —</option>
</select>
</div>
<div class="row">
<div class="col-6 mb-3">
<label class="form-label fw-bold">排序值</label>
<input type="number" id="catSort" class="form-control" value="0">
</div>
<div class="col-6 mb-3">
<label class="form-label fw-bold">状态</label>
<select id="catStatus" class="form-select">
<option value="1">启用</option>
<option value="0">禁用</option>
</select>
</div>
</div>
<div class="mb-3">
<label class="form-label fw-bold">描述</label>
<textarea id="catDesc" class="form-control" rows="2" placeholder="栏目描述（可选）"></textarea>
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
  fetch(ctx + '/category/tree').then(r => r.json()).then(res => {
    const all = flatten(res.data);
    const tb = document.getElementById('tbody');
    tb.innerHTML = '';
    renderTree(res.data, 0, tb);
    document.getElementById('totalInfo').textContent = '共 ' + all.length + ' 个栏目';
    loadParentOptions();
  });
}

function flatten(list) {
  let r = [];
  (list || []).forEach(c => { r.push(c); if (c.children) r = r.concat(flatten(c.children)); });
  return r;
}

function renderTree(list, depth, container) {
  (list || []).forEach(c => {
    const tr = document.createElement('tr');
    tr.innerHTML =
      '<td><span class="badge-count">' + c.id + '</span></td>' +
      '<td>' + (depth > 0 ? '<span class="tree-indent" style="width:' + (depth*20) + 'px;border-left:2px dashed #ccc;margin-right:8px;display:inline-block;height:1px"></span>' : '') +
      '<strong>' + c.catName + '</strong></td>' +
      '<td class="text-muted">' + (c.parentId === 0 ? '根' : c.parentId) + '</td>' +
      '<td>' + c.sort + '</td>' +
      '<td>' + (c.status === 1 ? '<span class="status-on">启用</span>' : '<span class="status-off">禁用</span>') + '</td>' +
      '<td class="text-muted">' + (c.catDesc || '') + '</td>' +
      '<td>' +
        '<button class="btn btn-sm btn-outline-primary me-1" onclick="openAddModal(' + c.id + ')">+子</button>' +
        '<button class="btn btn-sm btn-outline-secondary me-1" onclick="openEditModal(' + c.id + ')">编辑</button>' +
        '<button class="btn btn-sm ' + (c.status === 1 ? 'btn-outline-warning' : 'btn-outline-success') + ' me-1" onclick="toggleStatus(' + c.id + ')">' + (c.status === 1 ? '禁用' : '启用') + '</button>' +
        '<button class="btn btn-sm btn-outline-danger" onclick="del(' + c.id + ')">删除</button>' +
      '</td>';
    container.appendChild(tr);
    if (c.children && c.children.length) renderTree(c.children, depth + 1, container);
  });
}

function loadParentOptions() {
  fetch(ctx + '/category/tree').then(r => r.json()).then(res => {
    const sel = document.getElementById('parentSelect');
    const currentVal = sel.value;
    sel.innerHTML = '<option value="0">— 根栏目 —</option>';
    appendOptions(res.data, '', sel);
    sel.value = currentVal || '0';
  });
}

function appendOptions(list, prefix, sel) {
  (list || []).forEach(c => {
    const o = document.createElement('option');
    o.value = c.id;
    o.textContent = prefix + c.catName;
    sel.appendChild(o);
    if (c.children) appendOptions(c.children, prefix + '└ ', sel);
  });
}

function openAddModal(parentId) {
  document.getElementById('modalTitle').textContent = parentId ? '新增子栏目' : '新增根栏目';
  document.getElementById('catId').value = '';
  document.getElementById('catName').value = '';
  document.getElementById('catSort').value = '0';
  document.getElementById('catStatus').value = '1';
  document.getElementById('catDesc').value = '';
  loadParentOptions();
  document.getElementById('parentSelect').value = parentId || 0;
  document.getElementById('parentSelect').disabled = false;
  modal.show();
}

function openEditModal(id) {
  fetch(ctx + '/category/' + id).then(r => r.json()).then(res => {
    const c = res.data;
    document.getElementById('modalTitle').textContent = '编辑栏目';
    document.getElementById('catId').value = c.id;
    document.getElementById('catName').value = c.catName;
    document.getElementById('catSort').value = c.sort;
    document.getElementById('catStatus').value = c.status;
    document.getElementById('catDesc').value = c.catDesc || '';
    loadParentOptions();
    document.getElementById('parentSelect').value = c.parentId || 0;
    modal.show();
  });
}

function save() {
  const id = document.getElementById('catId').value;
  const data = {
    catName: document.getElementById('catName').value,
    parentId: parseInt(document.getElementById('parentSelect').value),
    sort: parseInt(document.getElementById('catSort').value),
    status: parseInt(document.getElementById('catStatus').value),
    catDesc: document.getElementById('catDesc').value
  };
  if (!data.catName.trim()) { alert('请输入栏目名称'); return; }

  const url = id ? ctx + '/category/update' : ctx + '/category/add';
  const params = new URLSearchParams(data);
  if (id) params.set('id', id);

  fetch(url, {
    method: id ? 'PUT' : 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: params.toString()
  }).then(r => r.json()).then(res => {
    if (res.code === 200) { modal.hide(); refresh(); }
    else alert(res.msg || '操作失败');
  });
}

function toggleStatus(id) {
  fetch(ctx + '/category/toggle/' + id, {method: 'PUT'}).then(r => r.json()).then(res => {
    if (res.code === 200) refresh(); else alert(res.msg);
  });
}

function del(id) {
  if (!confirm('确定删除此栏目？（该栏目下的文章 cat_id 会被置空）')) return;
  fetch(ctx + '/category/' + id, {method: 'DELETE'}).then(r => r.json()).then(res => {
    if (res.code === 200) refresh(); else alert(res.msg);
  });
}
</script>
</body>
</html>