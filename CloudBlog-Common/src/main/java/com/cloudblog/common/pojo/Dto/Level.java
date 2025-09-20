package com.cloudblog.common.pojo.Dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("level")
public class Level {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer level;

    private String levelName;

    private Integer expThreshold;
}
