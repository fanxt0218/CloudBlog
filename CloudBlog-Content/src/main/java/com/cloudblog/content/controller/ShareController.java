package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
