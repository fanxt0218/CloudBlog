package com.cloudblog.common.pojo.Dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("posts_content")
public class PostsContent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 0：纯文本 1：markdown 2：html
     */
    private Integer contentType;

    private String content;
}
