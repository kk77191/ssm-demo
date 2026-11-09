package com.lab.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.common.BusinessException;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.entity.CmsArticleTag;
import com.lab.cms.entity.CmsCategory;
import com.lab.cms.entity.CmsTag;
import com.lab.cms.mapper.CmsArticleMapper;
import com.lab.cms.mapper.CmsArticleTagMapper;
import com.lab.cms.mapper.CmsCategoryMapper;
import com.lab.cms.mapper.CmsTagMapper;
import com.lab.cms.service.CmsArticleService;
import com.lab.cms.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements CmsArticleService {

    @Autowired
    private CmsCategoryMapper categoryMapper;

    @Autowired
    private CmsArticleTagMapper articleTagMapper;

    @Autowired
    private CmsTagMapper tagMapper;

    /** 一次请求最多允许的排序字段数，防止参数被滥用 */
    private static final int MAX_SORT_FIELDS = 3;

    /**
     * 排序字段白名单：key 为归一化后的前端字段名，value 为 Lambda 列引用。
     * <p>
     * 排序字段来自请求参数，若直接拼进 SQL 会造成注入风险，因此这里用白名单做收敛：
     * 只有登记在册的字段才允许排序，其余一律忽略。
     */
    private static final Map<String, SFunction<CmsArticle, ?>> SORT_WHITELIST = new LinkedHashMap<>();

    static {
        SORT_WHITELIST.put("id", CmsArticle::getId);
        SORT_WHITELIST.put("title", CmsArticle::getTitle);
        SORT_WHITELIST.put("author", CmsArticle::getAuthor);
        SORT_WHITELIST.put("status", CmsArticle::getStatus);
        SORT_WHITELIST.put("createtime", CmsArticle::getCreateTime);
        SORT_WHITELIST.put("updatetime", CmsArticle::getUpdateTime);
    }

    @Override
    public IPage<ArticleVO> search(Integer pageNum, Integer pageSize, String keyword, Integer catId,
                                   Integer status, String author, LocalDate beginDate, LocalDate endDate,
                                   String sort, String order) {
        // 参数自检：起始日期晚于截止日期属于用户输入错误，直接抛业务异常由全局异常处理器转成提示
        if (beginDate != null && endDate != null && beginDate.isAfter(endDate)) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(CmsArticle::getTitle, keyword.trim())
                    .or().like(CmsArticle::getContent, keyword.trim()));
        }
        if (catId != null) {
            wrapper.eq(CmsArticle::getCatId, catId);
        }
        if (status != null) {
            wrapper.eq(CmsArticle::getStatus, status);
        }
        if (author != null && !author.trim().isEmpty()) {
            wrapper.like(CmsArticle::getAuthor, author.trim());
        }
        if (beginDate != null) {
            wrapper.ge(CmsArticle::getCreateTime, beginDate.atStartOfDay());
        }
        if (endDate != null) {
            // 用 "小于次日 0 点" 代替 "小于等于当天 23:59:59"：
            // 既能完整包含截止日一整天，也避免了时间字段精度带来的边界丢数据，且可以走索引
            wrapper.lt(CmsArticle::getCreateTime, endDate.plusDays(1).atStartOfDay());
        }
        applySort(wrapper, sort, order);
        IPage<CmsArticle> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 关联数据（栏目名、标签名）统一批量装配，避免逐条查询造成 N+1
        Page<ArticleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(toVoList(page.getRecords()));
        return voPage;
    }

    /**
     * 把文章实体批量装配成列表 VO。
     * <p>
     * 关键点：整页只发 3 条 SQL —— 栏目一次、文章标签关联一次、标签名一次，
     * 与页大小无关。若按"查一篇文章 → 查它的栏目 → 查它的标签"的直觉写法，
     * 10 条文章就会放大成 1 + 10 + 10 + 标签若干条，即典型的 N+1 问题。
     */
    private List<ArticleVO> toVoList(List<CmsArticle> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 本页涉及的栏目：一次 IN 查询取回 id -> 名称
        Set<Integer> catIds = records.stream()
                .map(CmsArticle::getCatId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, String> catNameMap = catIds.isEmpty() ? Collections.emptyMap()
                : categoryMapper.selectBatchIds(catIds).stream()
                    .collect(Collectors.toMap(CmsCategory::getId, CmsCategory::getCatName, (a, b) -> a));

        // 2. 本页文章的标签关联：一次 IN 查询
        List<Integer> articleIds = records.stream().map(CmsArticle::getId).collect(Collectors.toList());
        List<CmsArticleTag> relations = articleTagMapper.selectList(
                new LambdaQueryWrapper<CmsArticleTag>().in(CmsArticleTag::getArticleId, articleIds));

        // 3. 关联里出现的标签 id：一次 IN 查询取回 id -> 名称
        //    selectBatchIds 会带上 @TableLogic 的 deleted = 0 条件，已删除的标签不会出现在列表里
        Set<Integer> tagIds = relations.stream().map(CmsArticleTag::getTagId).collect(Collectors.toSet());
        Map<Integer, String> tagNameMap = tagIds.isEmpty() ? Collections.emptyMap()
                : tagMapper.selectBatchIds(tagIds).stream()
                    .collect(Collectors.toMap(CmsTag::getId, CmsTag::getTagName, (a, b) -> a));

        // 4. 在内存里按文章 id 分组，全程不再访问数据库
        Map<Integer, List<String>> tagsByArticle = relations.stream()
                .filter(r -> tagNameMap.containsKey(r.getTagId()))
                .collect(Collectors.groupingBy(CmsArticleTag::getArticleId,
                        Collectors.mapping(r -> tagNameMap.get(r.getTagId()), Collectors.toList())));

        return records.stream().map(a -> {
            ArticleVO vo = new ArticleVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setAuthor(a.getAuthor());
            vo.setCatId(a.getCatId());
            vo.setCatName(catNameMap.get(a.getCatId()));
            vo.setStatus(a.getStatus());
            vo.setCreateTime(a.getCreateTime());
            vo.setTags(tagsByArticle.getOrDefault(a.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 按前端传入的字段与方向追加 order by。
     * <p>
     * 支持多字段，例如 sort=status,createTime &amp;&amp; order=asc,desc
     * 会生成 ORDER BY status ASC, create_time DESC。
     */
    private void applySort(LambdaQueryWrapper<CmsArticle> wrapper, String sort, String order) {
        List<String> fields = split(sort);
        List<String> dirs = split(order);
        boolean hasId = false;
        int applied = 0;

        for (int i = 0; i < fields.size() && applied < MAX_SORT_FIELDS; i++) {
            String key = normalize(fields.get(i));
            SFunction<CmsArticle, ?> column = SORT_WHITELIST.get(key);
            if (column == null) {
                continue;   // 非白名单字段直接丢弃
            }
            // 方向缺省时按降序处理；显式写 asc/升序以外的值也按降序
            boolean asc = i < dirs.size() && isAsc(dirs.get(i));
            wrapper.orderBy(true, asc, column);
            if ("id".equals(key)) {
                hasId = true;
            }
            applied++;
        }

        // 兜底：没有命中任何白名单字段，或用户的排序里不含 id 时，追加 id 降序。
        // 保证同一排序值下的记录顺序稳定，分页翻页不会出现重复或漏数据。
        if (!hasId) {
            wrapper.orderByDesc(CmsArticle::getId);
        }
    }

    private static boolean isAsc(String dir) {
        return "asc".equalsIgnoreCase(dir) || "ascending".equalsIgnoreCase(dir);
    }

    /** createTime / create_time / CREATETIME 都归一化为 createtime */
    private static String normalize(String field) {
        return field.replace("_", "").trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> split(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public boolean batchUpdateStatus(List<Integer> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        LambdaUpdateWrapper<CmsArticle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(CmsArticle::getId, ids)
                .set(CmsArticle::getStatus, status)
                .set(CmsArticle::getUpdateTime, LocalDateTime.now());
        return baseMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean batchPublish(List<Integer> ids) {
        return batchUpdateStatus(ids, 1);
    }

    @Override
    public boolean batchOffShelf(List<Integer> ids) {
        return batchUpdateStatus(ids, 2);
    }
}
