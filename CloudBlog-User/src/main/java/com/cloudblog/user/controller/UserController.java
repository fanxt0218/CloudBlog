package com.cloudblog.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.user.mapper.UserMapper;
import com.cloudblog.user.pojo.dto.User;
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
        User result = userMapper.queryUserByNumber("0001");
        if (result != null){
            return result.toString();
        }else {
            return "用户不存在";
        }
    }
}
