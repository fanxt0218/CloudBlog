package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.PublishSharePo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/share")
public class ShareController {

    @Autowired
    private ShareService shareService;

    /**
     * 获取主页分享列表
     */
    @GetMapping("/getIndexShareList")
    public AjaxResult getIndexShareList(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer topicId
    ) {
        return shareService.getIndexShareList(cursor, size, topicId);
    }

    /**
     * 获取话题列表
     */
    @GetMapping("/getTopicList")
    public AjaxResult getTopicList() {
        return shareService.getTopicList();
    }

    /**
     * 获取创作页话题列表
     */
    @GetMapping("/getCreateTopicList")
    public AjaxResult getCreateTopicList() {
        return shareService.getPublishPageTopicList();
    }

    /**
     * 发布动态
     */
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody PublishSharePo po) {
        return shareService.publish(po);
    }

    /**
     * 查看动态
     */
    @GetMapping("/getShare")
    public AjaxResult getShare(@RequestParam Long shareId) {
        return shareService.getShare(shareId);
    }
}
