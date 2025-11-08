package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据账号查询用户
     * @param number
     * @return
     */
    User queryUserByNumber(String number);

    /**
     * 获取用户
     * @param loginType
     * @param target
     * @return
     */
    User getUser(@Param("loginType") String loginType, @Param("target") String target);
}
