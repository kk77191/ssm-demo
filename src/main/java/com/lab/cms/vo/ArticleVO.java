package com.lab.cms.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表视图对象。
 * <p>
 * 为什么不直接把 {@link com.lab.cms.entity.CmsArticle} 返回给前端：
 * <ol>
 *   <li>列表页不需要正文 content，字段裁剪能显著减少网络传输与内存占用；</li>
 *   <li>列表需要展示栏目名称、标签名称这类"关联表里的数据"，实体上没有这些字段，
 *       硬塞进去会污染持久化对象；</li>
 *   <li>VO 是稳定的对外契约，以后表结构变了只要改装配逻辑，前端不用动。</li>
 * </ol>
 */
@Data
public class ArticleVO {

    private Integer id;

    private String title;

    private String author;

    private Integer catId;

    /** 栏目名称，由关联查询装配 */
    private String catName;

    private Integer status;

    private LocalDateTime createTime;

    /** 标签名称集合，由关联查询装配 */
    private List<String> tags;
}
