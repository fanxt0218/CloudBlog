package com.cloudblog.content.service.impl;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.UploadUtil;
import com.cloudblog.content.service.CreateService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CreateServiceImpl implements CreateService {

    private final String UploadImagePath = "/contentFile/image";
    @Override
    public AjaxResult uploadImage(MultipartFile file) {
        String path = UploadUtil.uploadFile(file, UploadImagePath);
        return AjaxResult.success("上传成功", path);
    }
}
