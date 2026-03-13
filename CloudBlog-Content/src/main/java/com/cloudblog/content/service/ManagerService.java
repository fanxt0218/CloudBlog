package com.cloudblog.content.service;

import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.TagClass;
import com.cloudblog.common.pojo.DoMain.Topic;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.pojo.Po.ReviewOpinionPo;
import com.cloudblog.common.pojo.Po.UserListPo;
import com.cloudblog.common.result.AjaxResult;

import java.io.IOException;
import java.time.LocalDateTime;

public interface ManagerService {

    AjaxResult ContentReviewList(String title, LocalDateTime startTime, LocalDateTime endTime, Integer type, String author, Integer pageNum, Integer pageSize);

    AjaxResult ContentReview(Integer type, Long id, ReviewOpinionPo po);

    AjaxResult getContentList(ContentListManagePo po, ContentType type) throws IOException;

    AjaxResult editTag(Tag tag);

    AjaxResult editTagCategory(TagClass tagClass);

    AjaxResult editTopic(Topic topic);

    AjaxResult getUserList(UserListPo po);

    AjaxResult resetPassword(Long targetId);

    AjaxResult updateUserStatus(Long targetId, Integer status);

    AjaxResult addTag(Tag tag);

    AjaxResult addTagCategory(TagClass tagclass);

    AjaxResult addTopic(Topic topic);

    AjaxResult editUser(UserInfo userInfo);
}
