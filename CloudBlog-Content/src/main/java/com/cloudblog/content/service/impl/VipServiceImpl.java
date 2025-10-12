package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.Vo.UserVipInfoVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.VipMapper;
import com.cloudblog.content.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
