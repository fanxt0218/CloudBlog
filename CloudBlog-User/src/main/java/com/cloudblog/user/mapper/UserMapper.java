package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.User;

public interface UserMapper extends BaseMapper<User> {

    User queryUserByNumber(String number);
}
