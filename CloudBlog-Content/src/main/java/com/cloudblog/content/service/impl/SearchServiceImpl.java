package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.SearchMapper;
import com.cloudblog.content.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchMapper searchMapper;

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
}
