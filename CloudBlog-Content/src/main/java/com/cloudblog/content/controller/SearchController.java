package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import com.cloudblog.common.pojo.Po.DeleteSearchHistoryPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 获取搜索历史
     */
    @GetMapping("/getSearchHistory")
    public AjaxResult getSearchHistory(@RequestParam Long userId) {
        return searchService.getSearchHistory(userId);
    }

    /**
     * 添加搜索记录
     */
    @PostMapping("/addSearchRecord")
    public AjaxResult addSearchRecord(@RequestBody UserSearchHistory po) {
        return searchService.addSearchRecord(po);
    }

    /**
     * 删除搜索记录
     */
    @PostMapping("/deleteSearchRecord")
    public AjaxResult deleteSearchRecord(@RequestBody DeleteSearchHistoryPo po) {
        return searchService.deleteSearchRecord(po);
    }

    /**
     * 清空搜索记录
     */
    @PostMapping("/clearSearchRecord")
    public AjaxResult clearSearchRecord(@RequestParam Long userId) {
        return searchService.clearUserSearchRecord(userId);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/searchUser")
    public AjaxResult searchUser(
            @RequestParam(required = false) Long userId,
            @RequestParam String keyword) {
        return searchService.searchUser(userId, keyword);
    }

    /**
     * 获取热搜词（暂时假实现）
     */
    @GetMapping("/getHotSearch")
    public AjaxResult getHotSearch() {
        return searchService.getHotSearch();
    }
}
