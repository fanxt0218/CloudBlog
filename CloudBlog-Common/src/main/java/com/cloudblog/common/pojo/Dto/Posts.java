package com.cloudblog.common.pojo.Dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("posts")
public class Posts {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long authorId;

    private String title;

    private String introduction;

    private String image;

    /**
     * 0：正常 1：删除
     */
    private Integer status;

    private Long contentId;

    /**
     * 用于扩展，比如可见范围
     */
    private Integer type;

    /**
     * 0:否 1：是
     */
    private Integer isVip;

    private Integer categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
