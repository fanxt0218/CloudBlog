package com.cloudblog.common.pojo.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IndexResourceVo {

    private Long id;

    private String resourceName;

    private String resourceUrl;

    private String resourceSize;

    private String resourceType;

    private String resourceFormat;

    private String resourceDescription;

    private String resourceTags;

    private Integer resourceStatus;

    private Long resourceCreator;

    private String resourceCreatorName;

    private String resourceCategoryAvatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resourceUpdateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resourceCreateTime;

    private Integer resourceIsPublic;

    private Long resourceBindContentId;

    private String resourceBindContentType;

    private Integer vipResource;

    private Integer downloadCount;
}
