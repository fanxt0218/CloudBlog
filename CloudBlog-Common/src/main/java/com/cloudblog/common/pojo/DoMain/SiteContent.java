package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@TableName("site_content")
public class SiteContent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 内容类型
     * 如：BANNER、ADVERTISEMENT、DEFAULT_AVATAR、NOTICE、POPUP 等
     */
    private String contentType;

    /**
     * 内容标识键
     * 用于唯一标识某个位置或用途，如：home_banner_1、default_avatar_male
     */
    private String contentKey;

    /**
     * 内容值
     * 可以是 URL、JSON、HTML 等，根据 content_format 决定
     */
    private String contentValue;

    /**
     * 内容格式
     * TEXT-文本、URL-链接、IMAGE-图片、VIDEO-视频、JSON-结构化数据、HTML-富文本
     */
    private String contentFormat;

    /**
     * 所属分类
     * 如：HOME-首页、USER-用户、SYSTEM-系统、MARKETING-营销
     */
    private String category;

    /**
     * 分组名称
     * 用于将相关内容归类，如：banners、avatars、notices
     */
    private String groupName;

    /**
     * 内容标题/名称
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 扩展属性 (JSON 格式)
     * 例如：{"linkUrl":"xxx","target":"_blank","priority":1}
     */
    @TableField(exist = false)
    private Map<String, Object> attributesMap; // Java 中操作用

    @JsonRawValue
    private String attributes; // 实际存入 DB 的 JSON 字符串

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 是否公开：0-不公开 1-公开
     */
    private Integer isPublic;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 更新人 ID
     */
    private Long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;





    // Getter：Map → JSON String
    public void setAttributesMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            this.attributes = "{}";
        } else {
            try {
                this.attributes = new ObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                log.error("序列化 attributes 失败", e);
                this.attributes = "{}";
            }
        }
    }

    // Setter：JSON String → Map
    public Map<String, Object> getAttributesMap() {
        if (this.attributes == null || this.attributes.isBlank() || "{}".equals(this.attributes)) {
            return new HashMap<>();
        }
        try {
            return new ObjectMapper().readValue(this.attributes, Map.class);
        } catch (Exception e) {
            log.error("反序列化 attributes 失败", e);
            return new HashMap<>();
        }
    }
}
