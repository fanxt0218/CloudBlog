package com.cloudblog.user.controller;

import com.cloudblog.common.pojo.Po.UserBrowseListPo;
import com.cloudblog.common.pojo.Po.UserCollectListPo;
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
     * 获取用户点赞信息列表
     */
    @PostMapping("/likeList")
    public AjaxResult getLikeList(@RequestBody UserLikeListPo po) {
        return userInfoService.getLikeList(po);
    }

    /**
     * 获取用户收藏夹列表
     */
    @GetMapping("/getUserFavorites")
    public AjaxResult getUserFavorites(@RequestParam Long userId) {
        return userInfoService.getUserFavorites(userId);
    }

    /**
     * 获取用户收藏列表
     */
    @PostMapping("/collectList")
    public AjaxResult getCollectList(@RequestBody UserCollectListPo po) {
        return userInfoService.getCollectList(po);
    }

    /**
     * 获取用户浏览历史列表
     */
    @PostMapping("/browseHistory")
    public AjaxResult getBrowseHistory(@RequestBody UserBrowseListPo po) {
        return userInfoService.getBrowseHistory(po);
    }

}
