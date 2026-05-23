package com.cloudblog.content.service;

import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

    AjaxResult getWorkOrderList(WorkOrderListPo po);

    AjaxResult handleWorkOrder(WorkOrder workOrder);

    AjaxResult getRedisValue(String key);

    AjaxResult refreshCache(List<String> keys);

    AjaxResult getIndexDefine();

    AjaxResult getTotalArticleCount();

    AjaxResult getDataBoardUser();

    AjaxResult getHotArticle(Integer limit);

    AjaxResult getComponentDefine(WebSiteComponentPo po);

    AjaxResult uploadWebSiteResource(String category, String contentType, MultipartFile file);

    AjaxResult editWebSiteComponent(EditWebSiteComponentPo po);

    AjaxResult addComponent(SiteContent siteContent);

    AjaxResult deleteComponent(Long id);

    AjaxResult getComponentDefineForUser(WebSiteComponentPo po);

    AjaxResult getRagText();

    AjaxResult editRagText(MultipartFile file);

    AjaxResult getWorkOrderDetail(WorkOrder workOrder);

    AjaxResult getSensitiveWords();

    AjaxResult addSensitiveWord(PostForbiddenWords word);

    AjaxResult editSensitiveWord(PostForbiddenWords word);

    AjaxResult delSensitiveWord(Long id);
}
