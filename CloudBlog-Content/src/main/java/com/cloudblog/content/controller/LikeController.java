package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/liking")
    public AjaxResult liking(
            @RequestParam Long userId,
            @RequestParam Long targetId,
            @RequestParam Integer status,
            @RequestParam Integer type) {
        return likeService.liking(userId, targetId, status, type);
    }
}
