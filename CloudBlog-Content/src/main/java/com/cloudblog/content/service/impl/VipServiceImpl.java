package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.enums.VipStatus;
import com.cloudblog.common.pojo.DoMain.UserVip;
import com.cloudblog.common.pojo.Vo.UserVipInfoVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.VipMapper;
import com.cloudblog.content.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VipServiceImpl implements VipService {

    @Autowired
    private VipMapper vipMapper;


    @Override
    public AjaxResult getUserVipInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        UserVipInfoVo userVipInfoVo = vipMapper.getUserVipInfo(userId);
        if (userVipInfoVo == null) {
            // 表示未开通过会员
            userVipInfoVo = new UserVipInfoVo();
            userVipInfoVo.setUserId(userId);
            userVipInfoVo.setStatus(1); // 失效状态
        }
        return AjaxResult.success(userVipInfoVo);
    }

    @Transactional
    @Override
    public AjaxResult openVip(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 判断当前是否是会员
        Long l = vipMapper.selectCount(new LambdaQueryWrapper<UserVip>().eq(UserVip::getUserId, userId).eq(UserVip::getStatus, 0));
        if (l > 0) {
            return AjaxResult.warn("您已经是会员");
        }
        // 默认开通1个月会员，后续可更改逻辑
        UserVip userVip = new UserVip();
        userVip.setUserId(userId);
        userVip.setExpiresTime(LocalDateTime.now().plusMonths(1));
        userVip.setCreateTime(LocalDateTime.now());
        vipMapper.insert(userVip);
        // 更改用户信息
        vipMapper.updateUserVipStatus(userId, VipStatus.OPENED.getCode());
        // TODO 后续考虑同步用户会员状态与会员信息
        return AjaxResult.success("开通会员成功");
    }

    @Transactional
    @Override
    public void checkUserVipInfo() {
        // 先查找所有过期会员记录
        List<Long> oodUsers = vipMapper.getOutOfDateVipRecord();
        // 处理所有无效会员信息记录
        vipMapper.refreshVipRecord();
        // 更新用户会员状态
        if (!oodUsers.isEmpty()) {
            vipMapper.refreshUserVipStatus(oodUsers);
        }
    }
}
