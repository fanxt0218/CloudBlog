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
     * 收藏文章
     */
    @PostMapping("/collecting")
    public AjaxResult collecting(
            @RequestParam Long userId,
            @RequestParam Long postId,
            @RequestParam Integer status) {
        return favoritesService.collecting(userId, postId,status);
    }

}
