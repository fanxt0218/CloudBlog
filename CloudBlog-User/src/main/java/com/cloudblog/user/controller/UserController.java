package com.cloudblog.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/getUser")
    public String getUser() {
        return userMapper.queryUserByNumber("0001").toString();
    }
}
