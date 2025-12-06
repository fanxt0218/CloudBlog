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

    /**
     * 获取用户成就
     * @param userId
     * @return
     */
    @GetMapping("/getUserAchievement")
    public AjaxResult getUserAchievement(@RequestParam Long userId) {
        return userInfoService.getUserAchievement(userId);
    }

    /**
     * 获取用户兴趣信息
     * @param userId
     * @return
     */
    @GetMapping("/getUserInterestInfo")
    public AjaxResult getInterestInfo(@RequestParam Long userId) {
        return userInfoService.getInterestInfo(userId);
    }

    /**
     * 获取用户分类信息
     * @param userId
     * @return
     */
    @RequestMapping("/getUserCategoryInfo")
    public AjaxResult getCategoryInfo(@RequestParam Long userId) {
        return userInfoService.getCategoryInfo(userId);
    }

    /**
     * 获取用户博客列表
     */
    @GetMapping("/getUserPostList")
    public AjaxResult getUserPostList(
            @RequestParam Long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String tag) {
        return userInfoService.getUserPostList(userId, cursor, size, sortBy, tag);
    }

    /**
     * 获取用户动态列表
     */
    @GetMapping("/getUserShareList")
    public AjaxResult getUserShareList(
            @RequestParam Long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String tag) {
        return userInfoService.getUserShareList(userId, cursor, size, sortBy, tag);
    }

    /**
     * 获取首页推荐用户
     */
    @GetMapping("/scoreBasedUsers")
    public AjaxResult getScoreBasedUserList(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer type) {
        return userInfoService.getIndexUserList(cursor, size, type);
    }

}
