package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.Po.InteractionTrendPo;
import com.cloudblog.common.pojo.Vo.InteractionTrendVo;
import com.cloudblog.common.pojo.Vo.UserCreatorCenterListVo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CreateMapper {

    /**
     * 获取点赞趋势
     * @param po
     */
    List<InteractionTrendVo.ContentTrend> getLikeTrend(@Param("po") InteractionTrendPo po);

    /**
     * 获取收藏趋势
     * @param po
     * @return
     */
    List<InteractionTrendVo.ContentTrend> getCollectTrend(@Param("po") InteractionTrendPo po);

    /**
     * 获取评论趋势
     * @param po
     * @return
     */
    List<InteractionTrendVo.ContentTrend> getCommentTrend(@Param("po") InteractionTrendPo po);

    /**
     * 获取粉丝趋势
     * @param po
     * @return
     */
    List<InteractionTrendVo.ContentTrend> getFanTrend(@Param("po") InteractionTrendPo po);

    /**
     * 获取内容列表
     * @param page
     * @param userId
     * @param type
     */
    IPage<UserCreatorCenterListVo> getContentList(Page<UserCreatorCenterListVo> page, @Param("userId") Long userId, @Param("type") Integer type);

}
