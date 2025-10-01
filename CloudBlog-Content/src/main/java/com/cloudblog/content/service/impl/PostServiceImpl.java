package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.Po.UserLikeListPo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public IPage<UserLikeListVo> getUserLikeList(UserLikeListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();
        Page<UserLikeListVo> page = new Page<>(pageNum, pageSize);
        IPage<UserLikeListVo> userLikeListVoList = postMapper.getUserLikeList(page,po.getUserId(), po.getBeginTime(), po.getEndTime(), ContentType.POST.ordinal());
        return userLikeListVoList;
    }
}
