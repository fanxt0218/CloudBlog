package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/vip")
public class VipController {

    @Autowired
    private VipService vipService;

    /**
     * 获取用户会员信息
     */
    @GetMapping("/getUserVipInfo")
    public AjaxResult getUserVipInfo(@RequestParam Long userId) {
        return vipService.getUserVipInfo(userId);
    }

    /**
     * 开通会员
     */
    @PostMapping("/openVip")
    public AjaxResult openVip(@RequestParam Long userId) {
        return vipService.openVip(userId);
    }
}
