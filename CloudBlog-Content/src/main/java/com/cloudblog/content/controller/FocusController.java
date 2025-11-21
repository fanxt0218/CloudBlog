package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.FocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
