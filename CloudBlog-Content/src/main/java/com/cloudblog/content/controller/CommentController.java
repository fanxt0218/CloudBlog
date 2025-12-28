package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论
     */
    @GetMapping("/getComments")
    public AjaxResult getComments(
            @RequestParam Long contentId,
            @RequestParam Integer type,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long userId
    ) {
        return AjaxResult.success(commentService.getComments(contentId, type, parentId, userId));
    }
}
