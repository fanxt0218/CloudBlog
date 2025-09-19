package com.cloudblog.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.result.AjaxResult;
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
    public AjaxResult hello() {
        return AjaxResult.success("hello");
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

    @GetMapping("/getUserList")
    public AjaxResult getUserList() {
        Page<User> page = new Page<>(1, 10);
        Page<User> result = userMapper.selectPage(page, null);
        return AjaxResult.success(result);
    }
}
