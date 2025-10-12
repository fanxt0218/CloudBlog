package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.pojo.DoMain.UserVip;
import com.cloudblog.common.pojo.Vo.UserVipInfoVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.VipMapper;
import com.cloudblog.content.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        return AjaxResult.success(userVipInfoVo);
    }

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
        userVip.setId(userId);
        userVip.setExpiresTime(LocalDateTime.now().plusMonths(1));
        userVip.setCreateTime(LocalDateTime.now());
        vipMapper.insert(userVip);
        return AjaxResult.success("开通会员成功");
    }
}
