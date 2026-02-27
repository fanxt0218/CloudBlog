package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.NotificationType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Dto.PostAndShareInfo;
import com.cloudblog.common.pojo.Po.ReviewOpinionPo;
import com.cloudblog.common.pojo.Vo.ContentReviewVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.ManagerMapper;
import com.cloudblog.content.service.ManagerService;
import com.cloudblog.content.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public AjaxResult ContentReviewList(String title, LocalDateTime startTime, LocalDateTime endTime, Integer type, String author, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;

        if (type == null) {
            type = 0;
        }

        Page<ContentReviewVo> page = new Page<>(pageNum, pageSize);
        IPage<ContentReviewVo> list = managerMapper.getContentReviewList(page, title, startTime, endTime, type, author, pageNum, pageSize);

        return AjaxResult.success(list);
    }

    @Transactional
    @Override
    public AjaxResult ContentReview(Integer type, Long id, ReviewOpinionPo po) {
        if (null == po.getOpinion()) {
            return AjaxResult.warn("请填写审核意见");
        }
        // 查询目标状态
        PostAndShareInfo contentInfo = managerMapper.getContentInfo(type, id);
        if (!Objects.equals(contentInfo.getStatus(), PostStatus.REVIEWING.getCode())) {
            return AjaxResult.warn("内容不存在或已被处理");
        }

        // 处理内容
        try {
            managerMapper.contentReview(type, id, po.getOpinion());
        } catch (Exception e) {
            log.error("处理内容失败");
            return AjaxResult.error("处理内容失败");
        }

        if (!Objects.equals(po.getOpinion(), PostStatus.PUBLISHED.getCode())) {
            // 审核未通过
            UserInfo userInfo = managerMapper.getContentAuthorInfo(type, id);
            if (userInfo != null) {
                // 发送消息
                Notification notification = new Notification();
                notification.setRecipientId(userInfo.getUserId());
                notification.setSenderId(1L);  // 管理账号id
                notification.setType(NotificationType.CHAT.getValue());
//                notification.setObjectType(type);
                notification.setObjectType(NotificationType.CHAT.getValue());
                notification.setObjectId(id);
                StringBuilder content = new StringBuilder("您的");
                if (type == ContentType.POST.ordinal()) {
                    content.append("文章");
                } else {
                    content.append("动态");
                }
                content.append("《").append(contentInfo.getIntro()).append("》");
                content.append("审核未通过");
                if (po.getReason() != null && !po.getReason().isEmpty()) {
                    content.append("，原因：").append(po.getReason());
                }
                if (type == ContentType.POST.ordinal()) {
                    content.append("文章已存入您的草稿箱，修改后可重新发布");
                }
                notification.setContent(content.toString());
                notification.setCreateTime(LocalDateTime.now());

                notificationService.addNotification(notification);
            }
        }
        return AjaxResult.success("处理成功", id);
    }
}
