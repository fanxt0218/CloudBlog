package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdatePersonalInfoPo {

    private Long userId;

    private String userName;

    private String userAccount;

    private Integer sex;

    private String image;

    private String introduction;

    private String region;

    private LocalDate birthDate;

    private String profession;

    private List<Integer> tags;
}
