package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.UserVip;
import com.cloudblog.common.pojo.Vo.UserVipInfoVo;

public interface VipMapper extends BaseMapper<UserVip> {

    /**
     * 获取用户会员信息
     * @param userId
     * @return
     */
    UserVipInfoVo getUserVipInfo(Long userId);
}
