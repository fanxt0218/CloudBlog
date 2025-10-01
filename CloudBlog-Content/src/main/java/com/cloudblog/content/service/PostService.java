package com.cloudblog.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudblog.common.pojo.Po.UserLikeListPo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;

import java.util.List;

public interface PostService {

    /**
     * 获取用户点赞文章列表
     * @param po
     * @return
     */
    IPage<UserLikeListVo> getUserLikeList(UserLikeListPo po);
}
