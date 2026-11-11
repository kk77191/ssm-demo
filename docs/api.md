# 内容管理系统（SSM）接口文档

> 版本：v1.0 ｜ 更新日期：2026-11-27 ｜ 维护：进阶拓展功能组

## 目录

- [1. 通用约定](#1-通用约定)
- [2. 认证与权限](#2-认证与权限)
- [3. 文章接口 /api/articles](#3-文章接口)
- [4. 栏目接口 /category](#4-栏目接口)
- [5. 标签接口 /tag](#5-标签接口)
- [6. 文章标签关联 /articleTag](#6-文章标签关联)
- [7. 操作日志 /log](#7-操作日志)
- [8. 页面路由](#8-页面路由)
- [9. 错误码](#9-错误码)
- [10. 已知问题与后续优化](#10-已知问题与后续优化)

---

## 1. 通用约定

### 1.1 基础地址

```
http://localhost:8080/{contextPath}
```

`contextPath` 以 IDEA 中 Tomcat 部署配置的「应用程序上下文」为准，本项目默认 `/untitled12`。

### 1.2 统一响应结构

所有接口返回 `Result<T>`：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {},
  "timestamp": 1732600000000
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| code | Integer | 业务状态码，200 表示成功，非 200 表示失败 |
| msg | String | 提示信息，失败时可直接展示给用户 |
| data | T | 业务数据，无返回值时为 null |
| timestamp | Long | 服务端响应时间戳（毫秒），便于排查问题与日志对账 |

### 1.3 分页结构

文章列表返回 `PageResult<T>`：

```json
{
  "total": 20,
  "pageNum": 1,
  "pageSize": 10,
  "pages": 2,
  "records": []
}
```

| 字段 | 说明 |
|---|---|
| total | 总记录数 |
| pageNum | 当前页码（从 1 开始） |
| pageSize | 每页条数 |
| pages | 总页数 |
| records | 当前页数据 |

> 栏目、标签、日志三个列表接口目前直接返回 MyBatis-Plus 的 `IPage` 原始结构（`records/total/size/current/pages`），
> 字段含义相同但字段名不同，见第 10 节。

### 1.4 请求编码

| 场景 | Content-Type | 说明 |
|---|---|---|
| 单条新增 / 修改 | `application/x-www-form-urlencoded` | 普通表单提交，参数名与实体属性同名 |
| 批量操作 | `application/json` | 请求体为 id 数组，如 `[1,2,3]` |
| 查询 | — | 参数拼在 URL 上 |

**注意**：Servlet 规范只对 POST 解析表单请求体。项目已注册 `FormContentFilter`，
因此在 PUT / DELETE 请求中携带表单体或查询参数同样能被 Controller 接收。

### 1.5 时间格式

统一为 `yyyy-MM-dd HH:mm:ss`，例如 `2026-11-27 10:30:00`。
日期区间参数只传日期部分，格式 `yyyy-MM-dd`。

---

## 2. 认证与权限

- 登录成功后，用户信息存入 `HttpSession`（key = `loginUser`），后续请求依靠 `JSESSIONID` 识别身份。
- 未登录访问受保护接口返回 `{"code":401,"msg":"未登录"}`。
- 标注 `@RequireRole("admin")` 的接口，非管理员访问返回 `{"code":403,...}`。
- 单条新增 / 修改接口另加 `@Log`，会把操作人、方法、参数、IP 写入 `sys_operation_log`。

### 2.1 登录

```
POST /login
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

响应：`code=200` 表示登录成功并已写入 session；`code=500, msg="账号密码错误"` 表示失败。

### 2.2 注册

```
POST /login/register
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | String | 是 | 用户名，不可重复 |
| password | String | 是 | 密码 |

### 2.3 退出

```
GET /login/logout
```

销毁 session，返回成功。

### 2.4 当前登录用户

```
GET /login/info
```

响应 `data` 为当前用户（不含密码）：

```json
{ "id": 1, "username": "admin", "role": "admin", "createTime": "2026-11-20 10:00:00" }
```

### 2.5 分配角色（管理员）

```
PUT /login/assignRole
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | Integer | 是 | 用户 id |
| role | String | 是 | `admin` / `user` |

---

## 3. 文章接口

基础路径 `/api/articles`，采用 RESTful 风格：路径表示资源，HTTP 动词表示操作。

### 3.1 分页查询文章

```
GET /api/articles
```

**权限**：需登录

| 参数 | 类型 | 必填 | 默认 | 说明 |
|---|---|---|---|---|
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页条数 |
| keyword | String | 否 | — | 关键词，模糊匹配标题或正文 |
| author | String | 否 | — | 作者，模糊匹配 |
| categoryId | Integer | 否 | — | 栏目 id |
| status | Integer | 否 | — | 0 草稿 / 1 已发布 / 2 已下架 |
| beginDate | String | 否 | — | 创建时间起，`yyyy-MM-dd`，含当天 00:00:00 |
| endDate | String | 否 | — | 创建时间止，`yyyy-MM-dd`，含当天 23:59:59 |
| sort | String | 否 | — | 排序字段，多字段用逗号分隔 |
| order | String | 否 | desc | 排序方向，与 sort 逐位对应，`asc`/`desc` |

**sort 允许的字段（白名单）**：`id`、`title`、`author`、`status`、`createTime`、`updateTime`。
非白名单字段会被忽略，最多生效 3 个字段。

示例：

```
GET /api/articles?pageNum=1&pageSize=10&status=1&beginDate=2026-11-01&endDate=2026-11-30&sort=status,createTime&order=asc,desc
```

响应 `data` 为 `PageResult`，`records` 元素结构：

```json
{
  "id": 20,
  "title": "SSM 整合教程",
  "author": "admin",
  "catId": 4,
  "catName": "后端架构",
  "status": 1,
  "createTime": "2026-11-22 20:23:37",
  "tags": ["Java", "SpringBoot"]
}
```

> 列表不返回正文 `content`，减少传输量；需要正文请调用详情接口。
> `catName` 与 `tags` 由服务端批量关联查询后装配，单次请求固定 5 条 SQL，不随页大小增长。

**错误示例**（起止日期颠倒）：

```json
{ "code": 500, "msg": "开始日期不能晚于结束日期", "timestamp": 1732600000000 }
```

### 3.2 查询文章详情

```
GET /api/articles/{id}
```

响应 `data` 为文章实体，含 `content`。逻辑删除的文章查询不到，返回 `data: null`。

### 3.3 新增文章

```
POST /api/articles
```

**权限**：管理员

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| title | String | 是 | 标题 |
| author | String | 否 | 作者，默认 admin |
| content | String | 否 | 正文 |
| catId | Integer | 否 | 所属栏目 |
| status | Integer | 否 | 默认 0 草稿 |

`createTime` / `updateTime` 由 `MetaObjectHandler` 自动填充，无需传递。

### 3.4 修改文章

```
PUT /api/articles/{id}
```

**权限**：管理员

路径中的 `id` 优先，请求体里的 id 会被覆盖，避免 id 不一致导致误更新。
只传需要修改的字段即可，未传字段保持原值（MyBatis-Plus 默认忽略 null 字段）。

### 3.5 删除文章

```
DELETE /api/articles/{id}
```

**权限**：管理员

采用**逻辑删除**：实际执行 `UPDATE cms_article SET deleted = 1 WHERE id = ? AND deleted = 0`，
数据仍保留在数据库，可通过 `UPDATE cms_article SET deleted = 0 WHERE id = ?` 恢复。

### 3.6 批量发布

```
PUT /api/articles/batch-publish
Content-Type: application/json

[1, 2, 3]
```

**权限**：管理员。把选中文章状态改为 1（已发布）。

### 3.7 批量下架

```
PUT /api/articles/batch-off-shelf
Content-Type: application/json

[1, 2, 3]
```

**权限**：管理员。把选中文章状态改为 2（已下架）。

### 3.8 批量删除

```
DELETE /api/articles/batch
Content-Type: application/json

[1, 2, 3]
```

**权限**：管理员。逻辑删除，数据库中 `deleted` 置 1。

---

## 4. 栏目接口

基础路径 `/category`。栏目支持多级（`parentId`），`parentId = 0` 表示顶级。

### 4.1 分页查询栏目

```
GET /category/list?pageNum=1&pageSize=10
```

响应为标准 MyBatis-Plus 分页结构。

### 4.2 查询栏目树

```
GET /category/tree
```

返回顶级栏目数组，每个节点带 `children`：

```json
[
  { "id": 1, "catName": "技术分享", "parentId": 0, "sort": 1, "status": 1, "children": [] }
]
```

### 4.3 查询栏目详情

```
GET /category/{id}
```

### 4.4 新增栏目

```
POST /category/add
```

**权限**：管理员。`parentId` 缺省 0，`sort` 缺省 0，`status` 缺省 1。

### 4.5 修改栏目

```
PUT /category/update
```

**权限**：管理员。参数同新增，需带 `id`。

### 4.6 删除栏目

```
DELETE /category/{id}
```

**权限**：管理员。逻辑删除。

### 4.7 切换栏目状态

```
PUT /category/toggle/{id}
```

**权限**：管理员。启用 ↔ 禁用。

### 4.8 移动栏目

```
PUT /category/move
```

**权限**：管理员。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | Integer | 是 | 被移动的栏目 id |
| parentId | Integer | 是 | 目标父栏目 id，0 表示移到顶级 |
| sort | Integer | 否 | 在兄弟节点中的序号 |

---

## 5. 标签接口

基础路径 `/tag`。

### 5.1 分页查询标签

```
GET /tag/list?pageNum=1&pageSize=10
```

### 5.2 查询全部标签

```
GET /tag/all
```

返回标签数组，用于下拉框、多选框渲染。

### 5.3 查询标签详情

```
GET /tag/{id}
```

### 5.4 新增标签

```
POST /tag/add
```

**权限**：管理员。参数 `tagName`。

### 5.5 修改标签

```
PUT /tag/update
```

**权限**：管理员。参数 `id`、`tagName`。

### 5.6 删除标签

```
DELETE /tag/{id}
```

**权限**：管理员。逻辑删除；已删除的标签不会出现在文章列表的标签里。

---

## 6. 文章标签关联

基础路径 `/articleTag`。对应中间表 `cms_article_tag(article_id, tag_id)`。

### 6.1 查询某篇文章的标签

```
GET /articleTag/getByArticle?articleId=1
```

返回关联记录数组（只含 `articleId`、`tagId`）。

### 6.2 查询某个标签下的文章

```
GET /articleTag/getByTag?tagId=1
```

### 6.3 绑定标签

```
POST /articleTag/bind
```

**权限**：管理员。参数 `articleId`、`tagId`。重复绑定会因主键冲突失败。

### 6.4 解绑标签

```
DELETE /articleTag/unbind?articleId=1&tagId=2
```

**权限**：管理员。

---

## 7. 操作日志

基础路径 `/log`。**整个控制器要求管理员权限**。
日志由 AOP 切面在标注 `@Log` 的方法执行后自动写入，不需要前端调用。

### 7.1 分页查询日志

```
GET /log/list?pageNum=1&pageSize=10
```

每条记录包含 `userId`、`username`、`operation`、`method`、`params`、`ip`、`createTime`。

### 7.2 删除单条日志

```
DELETE /log/{id}
```

### 7.3 清空日志

```
DELETE /log/clear
```

---

## 8. 页面路由

非接口，返回 JSP 视图，供浏览器直接访问。

| 路径 | 视图 | 说明 |
|---|---|---|
| `/article` | article.jsp | 文章管理 |
| `/category` | category.jsp | 栏目管理 |
| `/tag` | tag.jsp | 标签管理 |

---

## 9. 错误码

| code | 含义 | 常见场景 |
|---|---|---|
| 200 | 成功 | — |
| 400 | 参数错误 | 参数校验失败、日期区间颠倒 |
| 401 | 未登录 | session 过期或未登录 |
| 403 | 无权限 | 非管理员调用管理接口 |
| 500 | 业务异常 / 系统异常 | 账号密码错误、数据库异常 |

失败响应示例：

```json
{ "code": 403, "msg": "无权限访问", "timestamp": 1732600000000 }
```

---

## 10. 已知问题与后续优化

1. **分页结构不统一**：`/api/articles` 返回 `PageResult`（`pageNum/pages`），
   而 `/category/list`、`/tag/list`、`/log/list` 直接返回 MyBatis-Plus 的 `IPage`
   （`current/size/pages`）。后续统一为 `PageResult`。
2. **`Result.code` 与 HTTP 状态码不联动**：业务失败时 HTTP 状态码仍是 200，
   错误信息放在响应体的 `code` 字段里。前端需判断 `res.code !== 200`。
3. **`BusinessException` 的 code 未透出**：全局异常处理器统一返回 500，
   后续可改为读取异常自带的 code。
4. **登录拦截器放行项待清理**：`SpringMvcConfig` 中 `/article/**`、`/category/**` 等放行规则
   是 RESTful 改造前的遗留配置，当前实际生效的是 `/api/articles` 需要登录，
   放行项对接口路径已无作用，建议清理以免误解。
