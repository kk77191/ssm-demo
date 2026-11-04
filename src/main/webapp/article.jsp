<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>文章管理 - CMS</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{background:#f5f7fb;min-height:100vh}
.navbar{background:#fff!important;box-shadow:0 2px 10px rgba(0,0,0,.08)}
.navbar-brand{font-weight:700;font-size:20px;color:#667eea!important}
.page-title{font-size:24px;font-weight:700;color:#333;margin:25px 0 5px}
.page-subtitle{color:#888;font-size:14px;margin-bottom:25px}
.table-card{border:none;border-radius:12px;background:#fff;box-shadow:0 4px 20px rgba(0,0,0,.06);overflow:hidden}
.table thead th{background:linear-gradient(135deg,#f093fb,#f5576c);color:#fff;border:none;font-weight:600;font-size:14px;padding:15px}
.table tbody td{padding:14px 15px;vertical-align:middle;border-color:#f0f0f0;font-size:14px}
.table tbody tr:hover{background:#fff5f7}
.badge-count{background:#fff0f2;color:#f5576c;font-weight:600;border-radius:20px;padding:4px 12px;font-size:13px}
.cat-badge{background:#f3f0ff;color:#667eea;border-radius:20px;padding:4px 12px;font-size:13px;font-weight:500}
.back-btn{color:#667eea;text-decoration:none;font-weight:500}
.back-btn:hover{text-decoration:underline}
.btn-primary{background:linear-gradient(135deg,#f093fb,#f5576c);border:none;font-weight:500}
.btn-primary:hover{opacity:.9}
.action-bar{background:#fff;border-radius:12px;padding:15px 20px;box-shadow:0 4px 20px rgba(0,0,0,.06);margin-bottom:20px;display:flex;align-items:center;gap:10px;flex-wrap:wrap}
.search-box{background:#fff;border-radius:12px;padding:20px;box-shadow:0 4px 20px rgba(0,0,0,.06);margin-bottom:20px}
.status-draft{background:#f3f4f6;color:#6b7280;border-radius:20px;padding:4px 12px;font-size:12px;font-weight:600}
.status-pub{background:#dcfce7;color:#16a34a;border-radius:20px;padding:4px 12px;font-size:12px;font-weight:600}
.status-off{background:#fee2e2;color:#dc2626;border-radius:20px;padding:4px 12px;font-size:12px;font-weight:600}
.pagination-wrap{display:flex;align-items:center;justify-content:space-between;margin-top:15px}
.pagination-wrap .info{color:#888;font-size:13px}
</style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light">
<div class="container">
<a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">CMS 内容管理系统</a>
<div class="ms-auto">
<a href="${pageContext.request.contextPath}/index.jsp" class="back-btn">返回首页</a>
</div>
</div>
</nav>

<div class="container">
<h2 class="page-title">文章管理</h2>
<p class="page-subtitle">文章的增删改、批量发布/下架、多条件检索、审核状态管理</p>

<div class="search-box">
<div class="row g-3 align-items-end">
<div class="col-md-4">
<label class="form-label fw-bold" style="font-size:13px;color:#555">关键词</label>
<input type="text" id="sKeyword" class="form-control" placeholder="输入关键词搜索标题或内容">
</div>
<div class="col-md-3">
<label class="form-label fw-bold" style="font-size:13px;color:#555">所属栏目</label>
<select id="sCatId" class="form-select">
<option value="">全部栏目</option>
</select>
</div>
<div class="col-md-2">
<label class="form-label fw-bold" style="font-size:13px;color:#555">审核状态</label>
<select id="sStatus" class="form-select">
<option value="">全部</option>
<option value="0">草稿</option>
<option value="1">已发布</option>
<option value="2">已下架</option>
</select>
</div>
<div class="col-md-3">
<button class="btn btn-primary" onclick="doSearch()">搜索</button>
<button class="btn btn-outline-secondary" onclick="resetSearch()">重置</button>
</div>
</div>
</div>

<div class="action-bar">
<button class="btn btn-primary" onclick="openAddModal()">新增文章</button>
<button class="btn btn-outline-success btn-sm" onclick="batchAction('publish')">批量发布</button>
<button class="btn btn-outline-warning btn-sm" onclick="batchAction('off')">批量下架</button>
<button class="btn btn-outline-danger btn-sm" onclick="batchAction('delete')">批量删除</button>
<button class="btn btn-outline-secondary btn-sm" onclick="refresh()">刷新</button>
<span class="ms-auto text-muted" style="font-size:13px" id="selectInfo"></span>
</div>

<div class="table-card">
<table class="table table-hover mb-0">
<thead>
<tr>
<th style="width:40px"><input type="checkbox" id="checkAll" onchange="toggleAll(this)"></th>
<th style="width:70px">ID</th>
<th>标题</th>
<th style="width:100px">栏目</th>
<th style="width:90px">状态</th>
<th style="width:80px">作者</th>
<th style="width:180px">操作</th>
</tr>
</thead>
<tbody id="tbody"></tbody>
</table>
<div class="pagination-wrap px-3 pb-3">
<span class="info" id="pageInfo"></span>
<div id="pageBtns"></div>
</div>
</div>
</div>

<div class="modal fade" id="editModal" tabindex="-1">
<div class="modal-dialog modal-dialog-centered modal-lg">
<div class="modal-content">
<div class="modal-header" style="background:linear-gradient(135deg,#f093fb,#f5576c);color:#fff;border:none">
<h5 class="modal-title" id="modalTitle">新增文章</h5>
<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
</div>
<div class="modal-body p-4">
<input type="hidden" id="artId">
<div class="row">
<div class="col-8 mb-3">
<label class="form-label fw-bold">标题</label>
<input type="text" id="artTitle" class="form-control" required>
</div>
<div class="col-4 mb-3">
<label class="form-label fw-bold">作者</label>
<input type="text" id="artAuthor" class="form-control" value="admin">
</div>
</div>
<div class="row">
<div class="col-6 mb-3">
<label class="form-label fw-bold">所属栏目</label>
<select id="artCatId" class="form-select"></select>
</div>
<div class="col-6 mb-3">
<label class="form-label fw-bold">状态</label>
<select id="artStatus" class="form-select">
<option value="0">草稿</option>
<option value="1">已发布</option>
<option value="2">已下架</option>
</select>
</div>
</div>
<div class="mb-3">
<label class="form-label fw-bold">内容</label>
<textarea id="artContent" class="form-control" rows="6" required></textarea>
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
let modal, pageNum = 1, pageSize = 5, totalPages = 1;
const ids_selected = new Set();
window.addEventListener('DOMContentLoaded', () => {
  modal = new bootstrap.Modal(document.getElementById('editModal'));
  loadCatOptions();
  refresh();
});

function loadCatOptions() {
  fetch(ctx + '/category/tree', {credentials: 'same-origin'}).then(r => r.json()).then(res => {
    const s1 = document.getElementById('sCatId'), s2 = document.getElementById('artCatId');
    s1.innerHTML = '<option value="">全部栏目</option>';
    s2.innerHTML = '';
    const flat = flattenTree(res.data);
    flat.forEach(c => {
      s1.innerHTML += '<option value="' + c.id + '">' + c.catName + '</option>';
      s2.innerHTML += '<option value="' + c.id + '">' + c.catName + '</option>';
    });
  }).catch(err => { console.error('loadCatOptions error', err); });
}

function flattenTree(list, arr) {
  arr = arr || [];
  (list || []).forEach(c => { arr.push(c); if (c.children) flattenTree(c.children, arr); });
  return arr;
}

function refresh() { search(); }

function doSearch() { pageNum = 1; search(); }

function search() {
  const kw = document.getElementById('sKeyword').value.trim();
  const catId = document.getElementById('sCatId').value;
  const status = document.getElementById('sStatus').value;
  let url = ctx + '/api/articles?pageNum=' + pageNum + '&pageSize=' + pageSize;
  if (kw) url += '&keyword=' + encodeURIComponent(kw);
  if (catId) url += '&categoryId=' + catId;
  if (status !== '') url += '&status=' + status;

  fetch(url, {credentials: 'same-origin'}).then(r => r.json()).then(res => {
    console.log('search response:', res);
    if (res.code !== 200) {
      alert(res.msg || '请求失败');
      return;
    }
    const p = res.data;
    totalPages = p.pages;
    const tb = document.getElementById('tbody');
    tb.innerHTML = '';
    (p.records || []).forEach(a => {
      const tr = document.createElement('tr');
      tr.innerHTML =
        '<td><input type="checkbox" class="row-check" value="' + a.id + '" onchange="toggleOne(' + a.id + ',this)"></td>' +
        '<td><span class="badge-count">' + a.id + '</span></td>' +
        '<td><strong>' + a.title + '</strong></td>' +
        '<td><span class="cat-badge">栏目 ' + a.catId + '</span></td>' +
        '<td>' + statusHtml(a.status) + '</td>' +
        '<td>' + (a.author || '') + '</td>' +
        '<td>' +
          '<button class="btn btn-sm btn-outline-secondary me-1" onclick="openEditModal(' + a.id + ')">编辑</button>' +
          '<button class="btn btn-sm btn-outline-danger" onclick="del(' + a.id + ')">删除</button>' +
        '</td>';
      tb.appendChild(tr);
    });
    document.getElementById('pageInfo').textContent = '共 ' + p.total + ' 条, 第 ' + pageNum + '/' + totalPages + ' 页';
    renderPageBtns();
    updateSelectInfo();
  }).catch(err => {
    console.error('search error', err);
    alert('请求异常: ' + err.message);
  });
}

function statusHtml(s) {
  if (s === 1) return '<span class="status-pub">已发布</span>';
  if (s === 2) return '<span class="status-off">已下架</span>';
  return '<span class="status-draft">草稿</span>';
}

function renderPageBtns() {
  const wrap = document.getElementById('pageBtns');
  wrap.innerHTML = '';
  const prev = document.createElement('button');
  prev.className = 'btn btn-sm btn-outline-secondary me-1';
  prev.textContent = '上一页'; prev.disabled = pageNum <= 1;
  prev.onclick = () => { pageNum--; search(); };
  wrap.appendChild(prev);
  const next = document.createElement('button');
  next.className = 'btn btn-sm btn-outline-secondary';
  next.textContent = '下一页'; next.disabled = pageNum >= totalPages;
  next.onclick = () => { pageNum++; search(); };
  wrap.appendChild(next);
}

function resetSearch() {
  document.getElementById('sKeyword').value = '';
  document.getElementById('sCatId').value = '';
  document.getElementById('sStatus').value = '';
  doSearch();
}

function toggleAll(cb) {
  document.querySelectorAll('.row-check').forEach(c => { c.checked = cb.checked; toggleOne(parseInt(c.value), c); });
}
function toggleOne(id, cb) {
  if (cb.checked) ids_selected.add(id); else ids_selected.delete(id);
  updateSelectInfo();
}
function updateSelectInfo() {
  document.getElementById('selectInfo').textContent = ids_selected.size ? '已选择 ' + ids_selected.size + ' 篇' : '';
}

function batchAction(type) {
  if (!ids_selected.size) { alert('请先勾选文章'); return; }
  const confirmMsg = type === 'delete' ? '确定批量删除 ' + ids_selected.size + ' 篇文章?' :
                     type === 'publish' ? '确定批量发布 ' + ids_selected.size + ' 篇文章?' :
                     '确定批量下架 ' + ids_selected.size + ' 篇文章?';
  if (!confirm(confirmMsg)) return;

  const ids = Array.from(ids_selected);
  let url, method = 'PUT';
  if (type === 'publish') url = ctx + '/api/articles/batch-publish';
  else if (type === 'off') url = ctx + '/api/articles/batch-off-shelf';
  else { url = ctx + '/api/articles/batch'; method = 'DELETE'; }

  fetch(url, {
    method: method,
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(ids),
    credentials: 'same-origin'
  }).then(r => r.json()).then(res => {
    if (res.code === 200) { ids_selected.clear(); refresh(); }
    else alert(res.msg);
  });
}

function openAddModal() {
  document.getElementById('modalTitle').textContent = '新增文章';
  document.getElementById('artId').value = '';
  document.getElementById('artTitle').value = '';
  document.getElementById('artAuthor').value = 'admin';
  document.getElementById('artStatus').value = '0';
  document.getElementById('artContent').value = '';
  modal.show();
}

function openEditModal(id) {
  fetch(ctx + '/api/articles/' + id, {credentials: 'same-origin'}).then(r => r.json()).then(res => {
    const a = res.data;
    document.getElementById('modalTitle').textContent = '编辑文章';
    document.getElementById('artId').value = a.id;
    document.getElementById('artTitle').value = a.title;
    document.getElementById('artAuthor').value = a.author || '';
    document.getElementById('artCatId').value = a.catId;
    document.getElementById('artStatus').value = a.status;
    document.getElementById('artContent').value = a.content;
    modal.show();
  });
}

function save() {
  const id = document.getElementById('artId').value;
  const data = {
    title: document.getElementById('artTitle').value,
    author: document.getElementById('artAuthor').value,
    catId: parseInt(document.getElementById('artCatId').value),
    status: parseInt(document.getElementById('artStatus').value),
    content: document.getElementById('artContent').value
  };
  if (!data.title.trim() || !data.content.trim()) { alert('标题和内容不能为空'); return; }

  const url = id ? ctx + '/api/articles/' + id : ctx + '/api/articles';
  const params = new URLSearchParams(data);
  if (id) params.set('id', id);

  fetch(url, {
    method: id ? 'PUT' : 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: params.toString(),
    credentials: 'same-origin'
  }).then(r => r.json()).then(res => {
    if (res.code === 200) { modal.hide(); refresh(); }
    else alert(res.msg || '操作失败');
  });
}

function del(id) {
  if (!confirm('确定删除此文章?')) return;
  fetch(ctx + '/api/articles/' + id, {method: 'DELETE', credentials: 'same-origin'}).then(r => r.json()).then(res => {
    if (res.code === 200) refresh(); else alert(res.msg);
  });
}
</script>
</body>
</html>