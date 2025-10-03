package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorites")
public class Favorites {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long userId;

    private String favoritesName;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
