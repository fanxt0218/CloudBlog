package com.cloudblog.content.controller.admin;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import com.cloudblog.content.service.impl.ManagerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/data")
public class DataViewController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取平台总文章数
     */
    @GetMapping("/getTotalArticleCount")
    public AjaxResult getTotalArticleCount() {
        return managerService.getTotalArticleCount();
    }

    /**
     * 获取数据看板用户数据
     */
    @GetMapping("/getDataBoardUser")
    public AjaxResult getDataBoardUser() {
        return managerService.getDataBoardUser();
    }

    /**
     * 获取热门文章top10
     */
    @GetMapping("/getHotArticle")
    public AjaxResult getHotArticle(@RequestParam(defaultValue = "10",required = false) Integer limit) {
        return managerService.getHotArticle(limit);
    }
}
