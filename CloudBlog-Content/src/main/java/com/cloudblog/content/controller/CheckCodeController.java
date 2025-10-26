package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.CheckCodeUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/checkCode")
public class CheckCodeController {

    /**
     * 获取验证码
     */
    @PostMapping("/generateCheckCode")
    public AjaxResult generateCheckCode(
            @RequestParam Long userId,
            @RequestParam String target,
            @RequestParam String type
            ) {
        CheckCodeUtil.generateCheckCode(target, type);
        return AjaxResult.success("验证码已发送,请注意查收");
    }

    /**
     * 验证码校验
     */
    @PostMapping("/verifyCheckCode")
    public AjaxResult checkCheckCode(
            @RequestParam Long userId,
            @RequestParam String target,
            @RequestParam String checkCode,
            @RequestParam String type
    ) {
        return CheckCodeUtil.checkCheckCode(target, checkCode, type) ? AjaxResult.success("验证通过") : AjaxResult.warn("验证码错误");
    }
}
