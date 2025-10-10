package com.cloudblog.user.service;

import com.cloudblog.common.pojo.DoMain.User;
import com.cloudblog.common.pojo.Po.UserRegisterPo;
import com.cloudblog.common.result.AjaxResult;

public interface UserService {

    AjaxResult register(UserRegisterPo userPo);

    User getUser(Long userId);

    /**
     * 根据账号查询用户
     * @param userAccount
     * @return
     */
    User getUserByAccount(String userAccount);

    /**
     * 修改密码
     * @param updateUser
     */
    void updatePassword(User updateUser);

    /**
     * 根据手机号查询用户
     * @param newPhone
     * @return
     */
    User getUserByPhone(String newPhone);

    /**
     * 修改手机号
     * @param updateUser
     */
    void updatePhone(User updateUser);

    /**
     * 根据邮箱查询用户
     * @param newEmail
     * @return
     */
    User getUserByEmail(String newEmail);

    /**
     * 修改邮箱
     * @param updateUser
     */
    void updateEmail(User updateUser);
}
