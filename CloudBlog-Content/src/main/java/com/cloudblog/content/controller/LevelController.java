package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/level")
public class LevelController {

    @Autowired
    private LevelService levelService;

    /**
     * 获取等级列表
     */
    @GetMapping("/getLevelList")
    public AjaxResult getLevelList() {
         return AjaxResult.success(levelService.getLevelList());
    }
}
