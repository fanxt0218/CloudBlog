package com.cloudblog.user.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.user.service.UserInfoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userInfo/homePage")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取用户信息(顶栏部分)
     */
    @RequestMapping("/getUserInfo")
    public AjaxResult getUserInfo(@RequestParam Long userId) {
        return userInfoService.getUserInfo(userId);
    }

    @GetMapping("/getUserAchievement")
    public AjaxResult getUserAchievement(@RequestParam Long userId) {
        return userInfoService.getUserAchievement(userId);
    }
}
