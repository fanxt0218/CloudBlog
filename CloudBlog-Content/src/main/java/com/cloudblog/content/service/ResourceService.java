package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Po.GetIndexResourcePo;
import com.cloudblog.common.pojo.Po.PostPo;
import com.cloudblog.common.pojo.Po.UploadResourcePo;
import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    AjaxResult uploadFile(MultipartFile file);

    AjaxResult uploadResource(UploadResourcePo po);

    AjaxResult getResource(Long userId, Long contentId);

    AjaxResult updateResource(UploadResourcePo uploadResourcePo);

    AjaxResult deleteResource(UploadResourcePo uploadResourcePo);

    AjaxResult getResourceCategory();

    AjaxResult getIndexResource(GetIndexResourcePo po, String cursor, Integer size, String sortBy, String tag);
}
