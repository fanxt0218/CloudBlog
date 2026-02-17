package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.CreateNewFavoritesPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/collect")
public class CollectController {

    @Autowired
    private FavoritesService favoritesService;

    /**
     * 收藏/取消收藏 文章
     */
    @PostMapping("/collecting")
    public AjaxResult collecting(
            @RequestParam Long userId,
            @RequestParam Long postId,
            @RequestParam Integer status,
            @RequestParam(required = false) Integer favoriteId) {
        return favoritesService.collecting(userId, postId,status, favoriteId);
    }

    /**
     * 新建收藏夹
     */
    @PostMapping("/newFavorites")
    public AjaxResult newFavorites(@RequestBody CreateNewFavoritesPo po) {
        return favoritesService.newFavorites(po);
    }

    /**
     * 获取内容已被收藏的收藏夹
     */
    @GetMapping("/getTargetHasCollectedFavorites")
    public AjaxResult getTargetHasCollectedFavorites(@RequestParam Long userId, @RequestParam Long postId) {
        return favoritesService.getTargetHasCollectedFavorites(userId, postId);
    }

}
