package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.GetIndexResourcePo;
import com.cloudblog.common.pojo.Po.PostPo;
import com.cloudblog.common.pojo.Po.UploadResourcePo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
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

    /**
     * 获取资源
     */
    @GetMapping("/getResource")
    public AjaxResult getResource(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long contentId) {
        return resourceService.getResource(userId, contentId);
    }

    /**
     * 修改资源
     */
    @PostMapping("/updateResource")
    public AjaxResult updateResource(@RequestBody UploadResourcePo uploadResourcePo) {
        return resourceService.updateResource(uploadResourcePo);
    }

    /**
     * 删除资源
     */
    @PostMapping("/deleteResource")
    public AjaxResult deleteResource(@RequestBody UploadResourcePo uploadResourcePo) {
        return resourceService.deleteResource(uploadResourcePo);
    }

    /**
     * 获取资源分类
     */
    @GetMapping("/getResourceCategory")
    public AjaxResult getResourceCategory() {
        return resourceService.getResourceCategory();
    }

    /**
     * 首页资源
     */
    @PostMapping("/getIndexResource")
    public AjaxResult getIndexResource(
            @RequestBody GetIndexResourcePo po,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String tag) {
        return resourceService.getIndexResource(po, cursor, size, sortBy, tag);
    }

    /**
     * 搜索资源
     */
    @GetMapping("/searchResource")
    public AjaxResult searchResource(@RequestParam String keyword) {
        return resourceService.searchResource(keyword);
    }

    /**
     * 下载资源（前置校验）
     */
    @GetMapping("/downloadResource")
    public AjaxResult downloadResource(
            @RequestParam Long userId,
            @RequestParam Long resourceId
            ) {
        return resourceService.downloadResource(userId, resourceId);
    }

    /**
     * 下载资源
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam Long userId,
            @RequestParam String url,
            @RequestParam(required = false) String filename,
            @RequestParam String token
    ) {
        return resourceService.download(userId, url, filename, token);
    }

    /**
     * 获取精选资源
     */
    @GetMapping("/getSelectedResource")
    public AjaxResult getSelectedResource() {
        return resourceService.getSelectedResource();
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/getDetail")
    public AjaxResult getDetail(@RequestParam Long resourceId) {
        return resourceService.getDetail(resourceId);
    }
}
