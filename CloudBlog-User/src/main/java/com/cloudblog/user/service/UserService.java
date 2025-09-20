package com.cloudblog.user.service;

import com.cloudblog.common.pojo.Dto.User;
import com.cloudblog.common.pojo.Po.UserRegisterPo;
import com.cloudblog.common.result.AjaxResult;

public interface UserService {

    AjaxResult register(UserRegisterPo userPo);
}
