package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Po.UploadResourcePo;
import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

    AjaxResult uploadFile(MultipartFile file);

    AjaxResult uploadResource(UploadResourcePo po);
}
