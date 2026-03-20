package com.cloudblog.common.pojo.Vo;

import lombok.Data;

/**
 * 热门文章排行榜 VO
 */
@Data
public class HotArticleVo {
    
    /**
     * 文章 ID
     */
    private Long articleId;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章简介
     */
    private String introduction;
    
    /**
     * 文章封面图
     */
    private String image;
    
    /**
     * 作者 ID
     */
    private Long authorId;
    
    /**
     * 作者名称
     */
    private String authorName;
    
    /**
     * 浏览量
     */
    private Long browseCount;
    
    /**
     * 点赞数
     */
    private Long likeCount;
    
    /**
     * 收藏数
     */
    private Long collectCount;
    
    /**
     * 评论数
     */
    private Long commentCount;
    
    /**
     * 综合得分 (权重评分)
     */
    private Double totalScore;
}
