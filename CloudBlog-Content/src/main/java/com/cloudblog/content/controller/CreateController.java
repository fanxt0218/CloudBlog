package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.InteractionTrendPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.CreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 互动趋势
     */
    @PostMapping("/interactionTrend")
    public AjaxResult interactionTrend(@RequestBody InteractionTrendPo po) {
        return createService.interactionTrend(po);
    }

    /**
     *  粉丝趋势
     */
    @PostMapping("/fanTrend")
    public AjaxResult fanTrend(@RequestBody InteractionTrendPo po) {
        return createService.fanTrend(po);
    }

    /**
     * 创作内容列表
     */
    @GetMapping("/getCreateContentList")
    public AjaxResult getCreateContentList(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return createService.getCreateContentList(userId, type, pageNum, pageSize);
    }

}
