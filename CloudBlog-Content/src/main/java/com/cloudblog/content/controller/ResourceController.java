package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.UploadResourcePo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/content/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 上传文件
     */
    @PostMapping("/uploadFile")
    public AjaxResult uploadFile(@RequestParam MultipartFile file) {
        return resourceService.uploadFile(file);
    }

    /**
     * 上传资源
     */
    @PostMapping("/upload")
    public AjaxResult uploadResource(@RequestBody UploadResourcePo uploadResourcePo) {
        return resourceService.uploadResource(uploadResourcePo);
    }
}
