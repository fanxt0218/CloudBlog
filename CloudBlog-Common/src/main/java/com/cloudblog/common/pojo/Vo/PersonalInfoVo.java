package com.cloudblog.common.pojo.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PersonalInfoVo {

    private Long userId;

    private String userAccount;

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

    private Integer blogAge;

    private List<UserTagList> tags;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserTagList {

        private Integer tagId;

        private String tagName;
    }
}
