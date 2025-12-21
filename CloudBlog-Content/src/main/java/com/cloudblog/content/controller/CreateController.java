package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.CreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/content/create")
public class CreateController {

    @Autowired
    private CreateService createService;

    /**
     * 上传图片
     */
    @PostMapping("/uploadImage")
    public AjaxResult uploadImage(@RequestParam MultipartFile file) {
        return createService.uploadImage(file);
    }

    /**
     * 上传视频
     */
    @PostMapping("/uploadVideo")
    public AjaxResult uploadVideo(@RequestParam MultipartFile file) {
        return createService.uploadVideo(file);
    }

}
