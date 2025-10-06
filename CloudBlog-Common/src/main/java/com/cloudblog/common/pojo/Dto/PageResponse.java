package com.cloudblog.common.pojo.Dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    /**
     *  数据
     */
    private List<T> content;

    /**
     *  当前页
     */
    private Integer currentPage;

    /**
     *  每页大小
     */
    private Integer pageSize;

    /**
     *  总记录数
     */
    private Long totalElements;

    /**
     *  总页数
     */
    private Integer totalPages;

    /**
     *  是否有下一页
     */
    private Boolean hasNext;

    /**
     *  下一页的游标
     */
    private String nextCursor; // 游标分页标识
}
