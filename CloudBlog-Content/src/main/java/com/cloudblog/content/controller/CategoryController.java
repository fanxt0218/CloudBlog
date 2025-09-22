package com.cloudblog.content.controller;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取用户分类信息
     * @param userId
     * @return
     */
    @RequestMapping("/getUserCategoryInfo")
    public AjaxResult getCategoryInfo(@RequestParam Long userId) {
        return categoryService.getCategoryInfo(userId);
    }
}
