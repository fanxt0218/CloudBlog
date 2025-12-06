package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Vo.IndexUserListVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 获取用户列表
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<IndexUserListVo> getUserList(@Param("lastId") Long lastId, @Param("lastCreateTime") LocalDateTime lastCreateTime, @Param("i") int i);

    /**
     * 根据文章数和粉丝数计算得分的用户列表（游标分页）
     * @param lastScore 上一次的得分
     * @param lastUserId 上一次的用户ID
     * @param size 分页大小
     * @return 用户列表
     */
    List<IndexUserListVo> getScoreBasedUserList(
            @Param("lastScore") Double lastScore,
            @Param("lastUserId") Long lastUserId,
            @Param("size") int size);

    /**
     * 根据文章数计算得分的用户列表（游标分页）
     * @param lastPostCount
     * @param lastUserId
     * @param i
     * @return
     */
    List<IndexUserListVo> getPostCountBasedUserList(@Param("lastPostCount") Long lastPostCount, @Param("lastUserId") Long lastUserId, @Param("size") int i);

    /**
     * 根据经验数计算得分的用户列表（游标分页）
     * @param lastExp
     * @param lastUserId
     * @param i
     * @return
     */
    List<IndexUserListVo> getExpBasedUserList(@Param("lastExp") Integer lastExp, @Param("lastUserId") Long lastUserId, @Param("size") int i);

    /**
     * 根据粉丝数计算得分的用户列表（游标分页）
     * @param lastFansCount
     * @param lastUserId
     * @param i
     * @return
     */
    List<IndexUserListVo> getFansCountBasedUserList(@Param("lastFansCount") Integer lastFansCount, @Param("lastUserId") Long lastUserId, @Param("size") int i);
}
