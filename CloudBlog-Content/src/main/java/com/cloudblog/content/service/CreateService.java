package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Po.InteractionTrendPo;
import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

public interface CreateService {

    AjaxResult uploadImage(MultipartFile file);

    AjaxResult uploadVideo(MultipartFile file);

    AjaxResult interactionTrend(InteractionTrendPo po);

    AjaxResult fanTrend(InteractionTrendPo po);

    AjaxResult getCreateContentList(Long userId, Integer type, Integer pageNum, Integer pageSize);
}
