package com.cloudblog.content.service;

import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

public interface CreateService {

    AjaxResult uploadImage(MultipartFile file);

    AjaxResult uploadVideo(MultipartFile file);
}
