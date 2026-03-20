package com.cloudblog.content.controller.admin;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/es")
public class EsManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取索引定义
     */
    @RequestMapping("/getIndexDefine")
    public AjaxResult getIndexDefine() {
        return managerService.getIndexDefine();
    }
}
