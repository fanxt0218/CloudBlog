package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.UserVip;
import com.cloudblog.common.pojo.Vo.UserVipInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VipMapper extends BaseMapper<UserVip> {

    /**
     * 获取用户会员信息
     * @param userId
     * @return
     */
    UserVipInfoVo getUserVipInfo(Long userId);

    /**
     * 更新用户会员状态
     * @param userId
     * @param code
     */
    void updateUserVipStatus(@Param("userId") Long userId, @Param("code") Integer code);

    /**
     * 刷新会员记录
     */
    void refreshVipRecord();

    /**
     * 获取过期会员记录
     * @return
     */
    List<Long> getOutOfDateVipRecord();

    /**
     * 刷新用户会员状态
     * @param oodUsers
     */
    void refreshUserVipStatus(@Param("oodUsers") List<Long> oodUsers);
}
