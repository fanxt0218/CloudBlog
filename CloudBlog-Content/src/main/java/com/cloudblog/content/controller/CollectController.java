package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/collect")
public class CollectController {

    @Autowired
    private FavoritesService favoritesService;

    /**
     * 获取用户收藏夹列表
     */
    @GetMapping("/getUserFavorites")
    public AjaxResult getUserFavorites(@RequestParam Long userId) {
        return favoritesService.getUserFavorites(userId);
    }
}
