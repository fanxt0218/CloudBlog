package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import com.cloudblog.common.result.AjaxResult;

public interface SearchService {

    AjaxResult getSearchHistory(Long userId);

    AjaxResult addSearchRecord(UserSearchHistory po);
}
