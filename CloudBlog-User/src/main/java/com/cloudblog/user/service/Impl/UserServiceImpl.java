package com.cloudblog.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloudblog.common.enums.UserStatus;
import com.cloudblog.common.pojo.DoMain.User;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Po.LoginPo;
import com.cloudblog.common.pojo.Po.UserRegisterPo;
import com.cloudblog.common.pojo.Vo.LoginVo;
import com.cloudblog.common.pojo.Vo.UserRegisterVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.*;
import com.cloudblog.content.service.FavoritesService;
import com.cloudblog.user.mapper.UserInfoMapper;
import com.cloudblog.user.mapper.UserMapper;
import com.cloudblog.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private FavoritesService favoritesService;

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisTokenBlacklistUtil redisTokenBlacklistUtil;

    // 根据bean名称注入
    @Resource(name="commonJwtUtil")
    private JwtTool jwtTool;

    @Value("${cloudblog.jwt.tokenTTL}")
    private Duration tokenTTL;


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
        userInfo.setImage("/profile/avatar/default/defaultAvatar.png");
        userInfoMapper.insert(userInfo);

        // 初始化默认收藏夹
        favoritesService.initDefaultFavorites(user.getId());

        // 完善鉴权，返回token
        String token = jwtTool.createToken(user.getId(), tokenTTL);
        UserRegisterVo result = UserRegisterVo.builder()
                .userId(user.getId())
                .token(token)
                .build();
        return AjaxResult.success("注册成功", result);
    }

    @Override
    public User getUser(Long userId) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, userId));
    }

    @Override
    public User getUserByAccount(String userAccount) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount));
    }

    @Override
    public void updatePassword(User updateUser) {
        userMapper.updateById(updateUser);
    }

    @Override
    public User getUserByPhone(String newPhone) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, newPhone));
    }

    @Override
    public void updatePhone(User updateUser) {
        userMapper.updateById(updateUser);
    }

    @Override
    public User getUserByEmail(String newEmail) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, newEmail));
    }

    @Override
    public void updateEmail(User updateUser) {
        userMapper.updateById(updateUser);
    }

    @Override
    public AjaxResult cancellation(Long userId) {
        if (userId == null) {
            return AjaxResult.warn("用户ID不能为空");
        }
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.DISABLED.getValue());
        userMapper.updateById(user);
        // 弹出登录,前端+redis实现
        String token = httpServletRequest.getHeader("Authorization");
        // 将 token 加入黑名单，设置剩余有效期
        if (token != null && !token.isEmpty()) {
            redisTokenBlacklistUtil.addToBlacklist(token, tokenTTL);
        }

        return AjaxResult.success("注销成功");
    }

    @Override
    public AjaxResult login(LoginPo po) {
        if (!po.getPassword().equals(po.getTwicePassword())) {
            return AjaxResult.warn("两次输入的密码不一致");
        }
        // 判断用户是否存在
        User user = userMapper.getUser(po.getLoginType(), po.getTarget());
        if (user == null) {
            return AjaxResult.warn("用户不存在");
        }
        // 判断用户状态
        if (!user.getStatus().equals(UserStatus.NORMAL.getValue())) {
            return AjaxResult.warn("用户状态异常");
        }
        // 校验密码
        if (!PasswordUtil.checkPassword(po.getPassword(), user.getPassword())) {
            return AjaxResult.warn("密码错误");
        }
        // 判断是否是管理员登录（管理端）
        if (po.isAdmin() && !user.getPermissionId().equals(0)) {
            return AjaxResult.warn("暂无权限,请联系管理员");
        }
        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.update(user, new LambdaUpdateWrapper<User>().eq(User::getId, user.getId()));
        // 完善鉴权，返回token
        String token = jwtTool.createToken(user.getId(), tokenTTL);
        LoginVo loginVo = LoginVo.builder()
                .userId(user.getId())
                .token(token)
                .build();
        return AjaxResult.success("登录成功", loginVo);
    }

    @Override
    public Long getUsersCount() {
        return userMapper.selectCount(null);
    }

    @Override
    public AjaxResult checkcodeLogin(String target, String checkCode, String type) {
        boolean b = CheckCodeUtil.checkCheckCode(target, checkCode, type);
        if (b) {
            // 查询用户信息
            User user = userMapper.getUser(type, target);
            if (user == null) {
                return AjaxResult.warn("用户不存在");
            }
            // 更新登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.update(user, new LambdaUpdateWrapper<User>().eq(User::getId, user.getId()));
            // 完善鉴权，返回token
            String token = jwtTool.createToken(user.getId(), tokenTTL);
            LoginVo loginVo = LoginVo.builder()
                    .userId(user.getId())
                    .token(token)
                    .build();
            return AjaxResult.success("登录成功", loginVo);
        } else {
            return AjaxResult.warn("验证码错误");
        }
    }

    @Override
    public AjaxResult checkLogin() {
        Long userId = UserContext.getUser();
        if (userId != null) {
            return AjaxResult.success("已登录",true);
        } else {
            return AjaxResult.success("未登录",false);
        }
    }


    /**
     * 校验用户名
     *
     * @param userName
     * @return
     */

    public static Boolean checkUserName(String userName) {
        // 用户名只能由字母、数字下、划线、汉字组成，且3-16位
        return userName.matches("^[a-zA-Z0-9_\u4e00-\u9fa5]{3,16}$");
    }

    /**
     * 校验密码
     * @param password
     * @return
     */

    public static Boolean checkPassword(String password) {
        // 密码只能由字母、数字组成，且6-16位
        return password.matches("^[a-zA-Z0-9]{6,16}$");
    }

    /**
     * 校验手机号
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        // 手机号只能由数字组成，且11位
        return phone.matches("^[0-9]{11}$");
    }
}
