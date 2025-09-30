package com.cloudblog.user.controller;

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


}
