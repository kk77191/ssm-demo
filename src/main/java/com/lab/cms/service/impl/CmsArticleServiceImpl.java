package com.lab.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lab.cms.entity.CmsArticle;
import com.lab.cms.mapper.CmsArticleMapper;
import com.lab.cms.service.CmsArticleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements CmsArticleService {

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
    public IPage<CmsArticle> search(Integer pageNum, Integer pageSize, String keyword, Integer catId,
                                    Integer status, String sort, String order) {
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
        applySort(wrapper, sort, order);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
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
