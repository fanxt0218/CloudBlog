package com.cloudblog.user.controller;

import com.cloudblog.common.pojo.Po.LoginPo;
import com.cloudblog.common.pojo.Po.UserRegisterPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody UserRegisterPo userPo) {
        return userService.register(userPo);
    }

    /**
     * 注销账户
     */
    @PostMapping("/cancellation")
    public AjaxResult cancellation(@RequestParam Long userId) {
        return userService.cancellation(userId);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginPo po) {
        return userService.login(po);
    }

    /**
     * 验证码登录
     */
    @PostMapping("/checkCodeLogin")
    public AjaxResult checkCodeLogin(@RequestParam String target, @RequestParam String checkCode, @RequestParam String type) {
        return userService.checkcodeLogin(target, checkCode, type);
    }


}
