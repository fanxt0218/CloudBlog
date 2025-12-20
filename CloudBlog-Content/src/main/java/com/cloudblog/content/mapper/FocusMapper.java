package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.UserFocus;
import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.pojo.Vo.FocusAndFansListVo;
import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FocusMapper extends BaseMapper<UserFocus> {

    /**
     * 获取用户关注列表
     * @param userId
     * @return
     */
    List<UserFocusListVo> getUserFocusList(Long userId);

    /**
     * 获取用户粉丝列表
     * @param userId
     * @return
     */
    List<UserFanListVo> getUserFanList(Long userId);

    /**
     * 关注用户
     * @param po
     */
    void focusUser(FocusUserPo po);

    /**
     * 取消关注用户
     * @param po
     */
    void cancelFocusUser(FocusUserPo po);

    /**
     * 获取关注状态
     * @param po
     */
    Integer getFollowStatus(FocusUserPo po);

    /**
     * 获取关注和粉丝列表
     * @param userId
     * @param lastUserId
     * @param lastCreateTime
     * @param type
     * @param i
     * @return
     */
    List<FocusAndFansListVo> getFocusAndFansList(
            @Param("userId") Long userId,
            @Param("lastUserId") Long lastUserId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("type") Integer type,
            @Param("size") int i
    );
}
