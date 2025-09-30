package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("comment_content")
public class CommentContent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long commentId;

    private String content;
}
