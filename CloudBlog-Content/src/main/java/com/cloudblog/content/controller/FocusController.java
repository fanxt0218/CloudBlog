package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.FocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/focus")
public class FocusController {

    @Autowired
    private FocusService focusService;

    /**
     * 关注用户
     */
    @PostMapping("/followUser")
    public AjaxResult followUser(@RequestBody FocusUserPo po) {
        return focusService.followUser(po);
    }

    /**
     * 获取关注状态
     */
    @PostMapping("/getFollowStatus")
    public AjaxResult getFollowStatus(@RequestBody FocusUserPo po) {
        return focusService.getFollowStatus(po);
    }

    /**
     * 获取关注人的作品列表
     */
    @GetMapping("/getFocusArticleList")
    public AjaxResult getFocusArticleList(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        return focusService.getFocusArticleList(userId, type, cursor, size);
    }

    /**
     * 获取关注/粉丝列表
     */
    @GetMapping("/getFocusAndFansList")
    public AjaxResult getFocusAndFansList(
            @RequestParam Long userId,
            @RequestParam(required = false,defaultValue = "0") Integer type,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false,defaultValue = "10") Integer size
            ) {
        return focusService.getFocusAndFansList(userId, type, cursor, size);
    }
}
