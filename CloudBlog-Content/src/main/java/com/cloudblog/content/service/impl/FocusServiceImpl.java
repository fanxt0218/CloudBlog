package com.cloudblog.content.service.impl;

import com.cloudblog.common.enums.FocusOperationType;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.FocusMapper;
import com.cloudblog.content.mapper.NotificationMapper;
import com.cloudblog.content.service.FocusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FocusServiceImpl implements FocusService {

    @Autowired
    private FocusMapper focusMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<UserFocusListVo> getUserFocusList(Long userId) {
        return focusMapper.getUserFocusList(userId);
    }

    @Override
    public List<UserFanListVo> getUserFanList(Long userId) {
        return focusMapper.getUserFanList(userId);
    }

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
                notification.setContent("关注了你");
                notification.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(notification);
            } catch (Exception e) {
                log.error("关注失败：{}", e.getMessage());
                return AjaxResult.error("关注失败");
            }
        } else {
            // 取消关注
            try {
                focusMapper.cancelFocusUser(po);
            } catch (Exception e) {
                log.error("取消关注失败：{}", e.getMessage());
                return AjaxResult.error("取消关注失败");
            }
        }
        return AjaxResult.success("操作成功");
    }
}
