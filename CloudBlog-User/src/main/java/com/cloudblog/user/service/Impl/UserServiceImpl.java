package com.cloudblog.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.pojo.DoMain.User;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Po.UserRegisterPo;
import com.cloudblog.common.pojo.Vo.UserRegisterVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.GenerateUserInfo;
import com.cloudblog.common.utils.PasswordUtil;
import com.cloudblog.user.mapper.UserInfoMapper;
import com.cloudblog.user.mapper.UserMapper;
import com.cloudblog.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Transactional
    @Override
    public AjaxResult register(UserRegisterPo userPo) {
        // 校验
        if (userPo.getUserName() == null || userPo.getUserName().isEmpty()){
            return AjaxResult.warn("用户名不能为空");
        }
        if (!checkUserName(userPo.getUserName())) {
            return AjaxResult.warn("用户名只能由3~16位的中英字符、数字、下划线组成");
        }
        if (userPo.getPassword() == null || userPo.getPassword().isEmpty()){
            return AjaxResult.warn("密码不能为空");
        }
        if (!checkPassword(userPo.getPassword())) {
            return AjaxResult.warn("密码只能由6~16位字母、数字组成");
        }
        if (userPo.getPhone() == null || userPo.getPhone().isEmpty()){
            return AjaxResult.warn("手机号不能为空");
        }
        if (!checkPhone(userPo.getPhone())) {
            return AjaxResult.warn("手机号只能由11位数字组成");
        }
        if (!userPo.getTwicePassword().equals(userPo.getPassword())){
            return AjaxResult.warn("两次输入的密码不一致");
        }

        // 验证是否存在
        User isExists = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userPo.getPhone()));
        if (isExists != null){
            return AjaxResult.warn("该手机号已注册");
        }

        // 新增用户
        User user = new User();
        // 生成用户账号
        user.setUserAccount(GenerateUserInfo.generateUserAccount());
        user.setPhone(userPo.getPhone());
        user.setPassword(PasswordUtil.hashPassword(userPo.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        user.setPermissionId(1);   // 后续完善权限
        userMapper.insert(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUserName(userPo.getUserName());
        userInfo.setCreateTime(LocalDateTime.now());
        userInfoMapper.insert(userInfo);

        // TODO 完善鉴权，返回token
        UserRegisterVo result = UserRegisterVo.builder()
                .id(user.getId())
                .token("")
                .build();
        return AjaxResult.success("注册成功", result);
    }

    @Override
    public User getUser(Long userId) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, userId));
    }


    /**
     * 校验用户名
     * @param userName
     * @return
     */
    public boolean checkUserName(String userName) {
        // 用户名只能由字母、数字下、划线、汉字组成，且3-16位
        return userName.matches("^[a-zA-Z0-9_\u4e00-\u9fa5]{3,16}$");
    }

    /**
     * 校验密码
     * @param password
     * @return
     */
    private boolean checkPassword(String password) {
        // 密码只能由字母、数字组成，且6-16位
        return password.matches("^[a-zA-Z0-9]{6,16}$");
    }

    private boolean checkPhone(String phone) {
        // 手机号只能由数字组成，且11位
        return phone.matches("^[0-9]{11}$");
    }
}
