package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/Interest")
public class InterestController {

    @Autowired
    private InterestService interestService;

    /**
     * 获取标签列表
     */
    @GetMapping("/tagList")
    public AjaxResult getTagList(
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
            ) {
        return interestService.getTagList(classId, tagName, pageNum, pageSize);
    }

    /**
     * 获取标签分类列表
     */
    @GetMapping("/tagClassList")
    public AjaxResult getTagClassList(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return interestService.getTagClassList(className, pageNum, pageSize);
    }

}
