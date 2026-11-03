package com.lab.cms.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一分页返回体。
 * <p>
 * 把 MyBatis-Plus 的 {@link IPage} 转换成前端友好的固定结构，
 * 避免不同接口返回的分页字段名不一致（有的叫 total，有的叫 count）。
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;
    /** 当前页码，从 1 开始 */
    private long pageNum;
    /** 每页条数 */
    private long pageSize;
    /** 总页数 */
    private long pages;
    /** 当前页数据 */
    private List<T> records;

    public PageResult() {
        this.records = new ArrayList<>();
    }

    public PageResult(long total, long pageNum, long pageSize, long pages, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.records = records == null ? new ArrayList<>() : records;
    }

    /**
     * 由 MyBatis-Plus 的 IPage 转换。
     *
     * @param page 分页查询结果，允许为 null
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        if (page == null) {
            return empty(1, 10);
        }
        return new PageResult<>(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getPages(),
                page.getRecords());
    }

    /**
     * 空分页结果，用于查询无数据时保持返回结构一致。
     */
    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        return new PageResult<>(0L, pageNum, pageSize, 0L, new ArrayList<>());
    }
}
