package com.cloudblog.user.controller;

import com.cloudblog.common.pojo.Po.*;
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
     * 修改用户信息详情(个人资料)
     */
    @PostMapping("/updatePersonalInfo")
    public AjaxResult updatePersonalInfo(@RequestBody UpdatePersonalInfoPo po) {
        return userInfoService.updatePersonalInfo(po);
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
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public AjaxResult updatePassword(@RequestBody UpdatePasswordPo po) {
        return userInfoService.updatePassword(po);
    }

    /**
     * 修改手机号
     */
    @PostMapping("/updatePhone")
    public AjaxResult updatePhone(@RequestBody UpdatePhonePo po) {
        return userInfoService.updatePhone(po);
    }

    /**
     * 修改邮箱
     */
    @PostMapping("/updateEmail")
    public AjaxResult updateEmail(@RequestBody UpdateEmailPo po) {
        return userInfoService.updateEmail(po);
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

    /**
     * 获取用户等级信息
     */
    @GetMapping("/userLevelInfo")
    public AjaxResult getUserLevelInfo(@RequestParam Long userId) {
        return userInfoService.getUserLevelInfo(userId);
    }

}
