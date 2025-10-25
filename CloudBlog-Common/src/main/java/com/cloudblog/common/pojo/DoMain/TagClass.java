package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tag_class")
public class TagClass {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String className;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
