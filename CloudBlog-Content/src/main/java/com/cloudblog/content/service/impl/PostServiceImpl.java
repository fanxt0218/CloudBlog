package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.Po.UserCollectListPo;
import com.cloudblog.common.pojo.Po.UserLikeListPo;
import com.cloudblog.common.pojo.Vo.UserCollectListVo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.FavoritesService;
import com.cloudblog.content.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private FavoritesService favoritesService;

    @Override
    public IPage<UserLikeListVo> getUserLikeList(UserLikeListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();
        Page<UserLikeListVo> page = new Page<>(pageNum, pageSize);
        return postMapper.getUserLikeList(page,po.getUserId(), po.getBeginTime(), po.getEndTime(), ContentType.POST.ordinal());
    }

    @Override
    public IPage<UserCollectListVo> getUserCollectList(UserCollectListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();
        // 提前处理情况
        po.setPostName(po.getPostName() == null || po.getPostName().trim().isEmpty() ? null : po.getPostName());
        po.setFavoritesId(po.getFavoritesId() == null ? favoritesService.getUserDefaultFavorites(po.getUserId()).getId() : po.getFavoritesId());
        Page<UserCollectListVo> page = new Page<>(pageNum, pageSize);
        return postMapper.getUserCollectList(page,po.getUserId(),po.getFavoritesId(),po.getPostName());
    }
}
