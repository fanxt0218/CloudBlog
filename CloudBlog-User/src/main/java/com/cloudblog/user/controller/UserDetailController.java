package com.cloudblog.user.controller;

import com.cloudblog.common.pojo.Po.UserLikeListPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo/detail")
public class UserDetailController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取用户信息详情(个人资料)
     * @param userId
     * @return
     */
    @GetMapping("/personalInfo")
    public AjaxResult getPersonalInfo(@RequestParam Long userId) {
        return userInfoService.getPersonalInfo(userId);
    }

    /**
     * 获取用户信息详情(账户设置)
     * @return
     */
    @GetMapping("/accountSettings")
    public AjaxResult getAccountSettings(@RequestParam Long userId) {
        return userInfoService.getAccountSettings(userId);
    }

    /**
     * 获取用户点赞信息
     */
    @GetMapping("/likeList")
    public AjaxResult getLikeList(@RequestBody UserLikeListPo po) {
        return userInfoService.getLikeList(po);
    }
}
