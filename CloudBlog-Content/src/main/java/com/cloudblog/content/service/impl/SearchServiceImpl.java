package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.pojo.Vo.HotSearchVo;
import com.cloudblog.common.pojo.Vo.SearchUserVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.SearchMapper;
import com.cloudblog.content.service.FocusService;
import com.cloudblog.content.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchMapper searchMapper;
    @Autowired
    private FocusService focusService;

    @Override
    public AjaxResult getSearchHistory(Long userId) {
        List<UserSearchHistory> userSearchHistory = searchMapper.getUserSearchHistory(userId);
        return AjaxResult.success(userSearchHistory);
    }

    @Override
    public AjaxResult addSearchRecord(UserSearchHistory po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (po.getKeyword() == null || po.getKeyword().isEmpty()) {
            return AjaxResult.error("搜索关键字不能为空");
        }
        po.setCreateTime(LocalDateTime.now());
        return searchMapper.addSearchRecord(po) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @Override
    public AjaxResult searchUser(Long userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return AjaxResult.error("搜索关键字不能为空");
        }
        List<SearchUserVo> users = searchMapper.searchUser(keyword.trim());

        // 判断是否关注
        if (userId != null) {
            for (SearchUserVo user : users) {
                AjaxResult followStatus = focusService.getFollowStatus(FocusUserPo.builder().userId(userId).focusUserId(user.getUserId()).build());
                user.setFollow((boolean)followStatus.get("data") ? 1 : 0);
            }
        } else {
            for (SearchUserVo user : users) {
                user.setFollow(0);
            }
        }
        return AjaxResult.success(users);
    }

    @Override
    public AjaxResult getHotSearch() {
        // 先假实现
        List<HotSearchVo> hotSearchVos = new ArrayList<>();
        hotSearchVos.add(new HotSearchVo("java操作es", 100));
        hotSearchVos.add(new HotSearchVo("矩阵螺旋矩阵", 98));
        hotSearchVos.add(new HotSearchVo("秒杀系统设计", 96));
        hotSearchVos.add(new HotSearchVo("nginx打不开", 95));
        hotSearchVos.add(new HotSearchVo("idea 指定pg驱动 jar", 94));
        hotSearchVos.add(new HotSearchVo("linux日常命令", 93));
        hotSearchVos.add(new HotSearchVo("系统镜像 csdn", 92));
        hotSearchVos.add(new HotSearchVo("mybatislog插件", 91));
        hotSearchVos.add(new HotSearchVo("minio开源吗", 90));
        hotSearchVos.add(new HotSearchVo("elasticsearch", 90));

        return AjaxResult.success(hotSearchVos);
    }
}
