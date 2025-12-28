package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Comments;
import com.cloudblog.common.pojo.Dto.UserSimpleInfo;
import com.cloudblog.common.pojo.Vo.CommentListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comments> {

    /**
     * 获取用户评论数量
     * @param userId
     * @return
     */
    Integer getUserCommentCount(Long userId);

    /**
     * 计算评论数量
     * @param contentId
     * @param type
     * @return
     */
    Long calculateCommentCount(@Param("contentId") Long contentId, @Param("type") Integer type);

    /**
     * 获取根评论
     * @param contentId
     * @param type
     * @return
     */
    List<CommentListVo> getRootComments(@Param("contentId") Long contentId, @Param("type") Integer type);

    /**
     * 获取子评论
     * @param parentId
     * @return
     */
    List<CommentListVo> getChildrenComments(Long parentId);

    /**
     * 获取评论点赞数量
     * @param commentId
     * @param type
     * @return
     */
    Long getCommentLikeCount(@Param("commentId") Long commentId, @Param("type") Integer type);

    /**
     * 获取评论点赞用户信息
     * @param commentId
     * @param type
     * @return
     */
    List<UserSimpleInfo> getCommentLikeUserInfo(@Param("commentId") Long commentId, @Param("type") Integer type);

    /**
     * 获取子评论数量
     * @param rootCommentId
     * @return
     */
    Long getChildrenCommentCount(Long rootCommentId);
}
