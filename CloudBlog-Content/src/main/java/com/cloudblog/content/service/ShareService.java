package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Po.PublishSharePo;
import com.cloudblog.common.pojo.Vo.IndexFocusArticleVo;
import com.cloudblog.common.result.AjaxResult;

import java.time.LocalDateTime;
import java.util.List;

public interface ShareService {

    AjaxResult getUserShareList(Long userId, String cursor, Integer size, String sortBy, String tag);

    /**
     * 添加动态浏览数
     * @param postId
     * @param userId
     */
    void addShareBrowseCount(Long postId, Long userId);

    AjaxResult getIndexShareList(String cursor, Integer size, Integer topicId);

    AjaxResult getTopicList();

    /**
     * 获取关注动态
     * @param lastTargetId
     * @param lastCreateTime
     * @param i
     * @param userId
     * @return
     */
    List<IndexFocusArticleVo> getFocusShareList(Long lastTargetId, LocalDateTime lastCreateTime, int i, Long userId);

    AjaxResult getPublishPageTopicList();

    AjaxResult publish(PublishSharePo po);

    AjaxResult getShare(Long shareId);
}
