package com.cloudblog.content.service.impl;

import com.cloudblog.common.enums.FocusOperationType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.pojo.Vo.IndexFocusArticleVo;
import com.cloudblog.common.pojo.Vo.IndexUserListVo;
import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.FocusMapper;
import com.cloudblog.content.mapper.NotificationMapper;
import com.cloudblog.content.service.FocusService;
import com.cloudblog.content.service.PostService;
import com.cloudblog.content.service.ShareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FocusServiceImpl implements FocusService {

    @Autowired
    private FocusMapper focusMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private PostService postService;
    @Autowired
    private ShareService shareService;

    @Override
    public List<UserFocusListVo> getUserFocusList(Long userId) {
        return focusMapper.getUserFocusList(userId);
    }

    @Override
    public List<UserFanListVo> getUserFanList(Long userId) {
        return focusMapper.getUserFanList(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult followUser(FocusUserPo po) {
        if (po.getUserId() == null || po.getFocusUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (po.getUserId().equals(po.getFocusUserId())) {
            return AjaxResult.error("不能关注自己");
        }
        if (po.getStatus() == null) {
            po.setStatus(0);
        }

        if (po.getStatus() == FocusOperationType.Follow.ordinal()){
            // 关注
            try {
                focusMapper.focusUser(po);
                // 发送通知
                Notification notification = new Notification();
                notification.setSenderId(po.getUserId());
                notification.setRecipientId(po.getFocusUserId());
                notification.setType(com.cloudblog.common.enums.NotificationType.NEW_FAN.getValue());
                notification.setContent("关注了你 来源["+po.getSource()+"]");
                notification.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(notification);
            } catch (Exception e) {
                log.error("关注失败：{}", e.getMessage());
                CloudBlogException.cast("关注失败");
            }
        } else {
            // 取消关注
            try {
                focusMapper.cancelFocusUser(po);
            } catch (Exception e) {
                log.error("取消关注失败：{}", e.getMessage());
                CloudBlogException.cast("取消关注失败");
            }
        }
        return AjaxResult.success("操作成功");
    }

    @Override
    public AjaxResult getFollowStatus(FocusUserPo po) {
        Integer followStatus = focusMapper.getFollowStatus(po);
        return followStatus <= 0 ? AjaxResult.success("未关注",false) : AjaxResult.success("已关注",true);
    }

    @Override
    public AjaxResult getFocusArticleList(Long userId, Integer type, String cursor, Integer size) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        type = type == null ? 0 : type;
        // 默认参数处理
        size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

        // 解析游标
        Map<String, Object> cursorMap = parseCursor(cursor);
        Long lastTargetId = null;
        LocalDateTime lastCreateTime = null;

        if (cursorMap != null) {
            Object targetObj = cursorMap.get("targetId");
            Object createTimeObj = cursorMap.get("createTime");

            if (targetObj != null && createTimeObj != null) {
                lastTargetId = Long.parseLong(String.valueOf(targetObj));
                if (createTimeObj instanceof LocalDateTime) {
                    lastCreateTime = (LocalDateTime) createTimeObj;
                } else if (createTimeObj instanceof List<?> timeList) {
                    // 如果是数组，则按格式重新构建LocalDateTime
                    if (timeList.size() >= 6) {
                        // LocalDateTime的数组形式通常是[year, month, day, hour, minute, second, nanosecond]
                        int year = ((Number) timeList.get(0)).intValue();
                        int month = ((Number) timeList.get(1)).intValue();
                        int day = ((Number) timeList.get(2)).intValue();
                        int hour = ((Number) timeList.get(3)).intValue();
                        int minute = ((Number) timeList.get(4)).intValue();
                        int second = ((Number) timeList.get(5)).intValue();
                        lastCreateTime = LocalDateTime.of(year, month, day, hour, minute, second);
                    }
                } else if (createTimeObj instanceof String) {
                    // 如果是字符串，则直接解析
                    lastCreateTime = LocalDateTime.parse((String) createTimeObj);
                }
            }
        }

        // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
        List<IndexFocusArticleVo> articles;
        if (type == 0) {
            articles = postService.getFocusPostList(lastTargetId, lastCreateTime, size + 1, userId);
        } else {
            articles = shareService.getFocusShareList(lastTargetId, lastCreateTime, size + 1, userId);
        }
        Integer finalType = type;
        articles.forEach(article -> {article.setType(finalType);});

        // 构建PageResponse返回结果
        PageResponse<IndexFocusArticleVo> response = new PageResponse<>();
        response.setPageSize(size);

        // 判断是否还有更多数据
        boolean hasNext = articles.size() > size;
        response.setHasNext(hasNext);

        // 设置实际返回的数据列表
        if (hasNext) {
            // 移除多查的一个元素
            response.setContent(articles.subList(0, size));
            // 生成下一个游标
            IndexFocusArticleVo lastArticle = articles.get(size - 1);
            String nextCursor = generateCursor(lastArticle);
            response.setNextCursor(nextCursor);
        } else {
            response.setContent(articles);
        }
        return AjaxResult.success(response);
    }

    /**
     * 解析游标字符串
     * @param cursor 游标字符串
     * @return 解析后的游标数据
     */
    private Map<String, Object> parseCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
//            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(decoded, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成游标字符串
     * @param article 当前内容
     * @return 游标字符串
     */
    private String generateCursor(IndexFocusArticleVo article) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("targetId", article.getArticleId());
            cursorMap.put("createTime", article.getCreateTime().toString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
