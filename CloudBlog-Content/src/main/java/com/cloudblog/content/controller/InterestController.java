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
     * 获取用户兴趣信息
     * @param userId
     * @return
     */
    @GetMapping("/getUserInterestInfo")
    public AjaxResult getInterestInfo(@RequestParam Long userId) {
        return interestService.getInterestInfo(userId);
    }
}
