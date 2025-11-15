package com.cloudblog.content.controller;

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

}
