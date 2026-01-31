package com.cloudblog.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.pojo.Vo.IndexFocusArticleVo;
import com.cloudblog.common.pojo.Vo.UserBrowseListVo;
import com.cloudblog.common.pojo.Vo.UserCollectListVo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;
import com.cloudblog.common.result.AjaxResult;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    /**
     * 获取用户点赞文章列表
     * @param po
     * @return
     */
    IPage<UserLikeListVo> getUserLikeList(UserLikeListPo po);

    /**
     * 获取用户收藏文章列表
     * @param po
     * @return
     */
    IPage<UserCollectListVo> getUserCollectList(UserCollectListPo po);

    /**
     * 获取用户浏览历史
     * @param po
     * @return
     */
    IPage<UserBrowseListVo> getUserBrowseHistory(UserBrowseListPo po);

    /**
     * 获取用户文章列表
     * @param cursor
     * @param size
     * @param sortBy
     * @param tag
     * @return
     */
    AjaxResult getUserPostList(Long userId, String cursor, Integer size, String sortBy, String tag);

    /**
     * 获取首页文章列表
     * @param po
     * @return
     */
    AjaxResult getIndexPostList(PostPo po, String cursor, Integer size, String sortBy, String tag);

    /**
     * 增加浏览
     * @param po
     * @return
     */
    AjaxResult addBrowseCount(AddBrowseCountPo po);

    /**
     * 获取关注文章列表
     * @param lastTargetId
     * @param lastCreateTime
     * @param i
     * @param userId
     * @return
     */
    List<IndexFocusArticleVo> getFocusPostList(Long lastTargetId, LocalDateTime lastCreateTime, int i, Long userId);

    /**
     * 发布文章
     * @param po
     * @return
     */
    AjaxResult publish(PublishPostPo po);

    /**
     * 获取文章
     * @param postId
     * @param userId
     * @return
     */
    AjaxResult getPost(Long postId, Long userId);

    AjaxResult getBrowseTopPostList(Integer postType);

    AjaxResult saveDraft(PublishPostPo po);

    AjaxResult getUserDraftList(Long userId);

    /**
     * 获取其他用户文章列表(他人用户主页)
     * @param userId
     * @param loginUserId
     * @param cursor
     * @param size
     * @param sortBy
     * @param tag
     * @return
     */
    AjaxResult getOtherUserPostList(Long userId, Long loginUserId, String cursor, Integer size, String sortBy, String tag);

    AjaxResult syncES() throws IOException, InterruptedException;
}
