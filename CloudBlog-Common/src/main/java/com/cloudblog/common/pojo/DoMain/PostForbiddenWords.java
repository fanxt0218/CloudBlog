package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("post_forbidden_words")
public class PostForbiddenWords {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String word;

    private String description;

    private String status;

    private String updateTime;

    private String createTime;



}
