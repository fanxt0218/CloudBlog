package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.Dto.Posts;
import com.cloudblog.common.pojo.Vo.UserAchievementVo;

import java.util.List;

public interface UserPostInfoMapper extends BaseMapper<Posts> {
    /**
     * 获取用户创作历程
     * @param userId
     * @return
     */
    List<UserAchievementVo.CreativeProcess> getUserCreativeProcess(Long userId);
}
