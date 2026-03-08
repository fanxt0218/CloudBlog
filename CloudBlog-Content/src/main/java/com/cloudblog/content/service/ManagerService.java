package com.cloudblog.content.service;

import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.pojo.Po.ReviewOpinionPo;
import com.cloudblog.common.result.AjaxResult;

import java.io.IOException;
import java.time.LocalDateTime;

public interface ManagerService {

    AjaxResult ContentReviewList(String title, LocalDateTime startTime, LocalDateTime endTime, Integer type, String author, Integer pageNum, Integer pageSize);

    AjaxResult ContentReview(Integer type, Long id, ReviewOpinionPo po);

    AjaxResult getContentList(ContentListManagePo po, ContentType type) throws IOException;
}
