package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_info")
public class UserInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String userName;

    /**
     * 0：女 1：男 2：未指定
     */
    private Integer sex;

    private String image;

    private String introduction;

    private String region;

    private LocalDate birthDate;

    private String profession;

    /**
     * 是否是会员(0:不是 1:是)
     */
    private Integer isVip;

    private Integer exp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
