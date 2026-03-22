package com.cloudblog.content.controller.admin;

import com.cloudblog.common.pojo.DoMain.SiteContent;
import com.cloudblog.common.pojo.Po.EditWebSiteComponentPo;
import com.cloudblog.common.pojo.Po.WebSiteComponentPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/website")
public class WebSiteManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取组件信息
     */
    @PostMapping("/getComponentDefine")
    public AjaxResult getComponentDefine(@RequestBody WebSiteComponentPo po) {
        return managerService.getComponentDefine(po);
    }

    /**
     * 上传资源
     */
    @PostMapping("/uploadResource")
    public AjaxResult uploadResource(
            @RequestParam String category,
            @RequestParam String contentType,
            @RequestParam("file") MultipartFile file
    ) {
        return managerService.uploadWebSiteResource(category, contentType, file);
    }

    /**
     * 修改首页组件
     */
    @PostMapping("/editIndexDefine")
    public AjaxResult editIndexDefine(@RequestBody EditWebSiteComponentPo po) {
        return managerService.editWebSiteComponent(po);
    }

    /**
     * 新增组件
     */
    @PostMapping("/addComponent")
    public AjaxResult addComponent(@RequestBody SiteContent siteContent) {
        return managerService.addComponent(siteContent);
    }

    /**
     * 删除组件
     */
    @PostMapping("/deleteComponent")
    public AjaxResult deleteComponent(@RequestParam Long id) {
        return managerService.deleteComponent(id);
    }

    /**
     * 获取组件配置信息（用户端）
     */
    @PostMapping("/getComponentDefineForUser")
    public AjaxResult getComponentDefineForUser(@RequestBody WebSiteComponentPo po) {
        return managerService.getComponentDefineForUser(po);
    }
}
