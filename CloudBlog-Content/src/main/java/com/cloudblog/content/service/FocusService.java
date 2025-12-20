package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;
import com.cloudblog.common.result.AjaxResult;

import java.util.List;

public interface FocusService {

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
     * @return
     */
    AjaxResult followUser(FocusUserPo po);

    /**
     * 获取关注状态
     * @param po
     * @return
     */
    AjaxResult getFollowStatus(FocusUserPo po);

    AjaxResult getFocusArticleList(Long userId, Integer type, String cursor, Integer size);

    AjaxResult getFocusAndFansList(Long userId, Integer type, String cursor, Integer size);
}
