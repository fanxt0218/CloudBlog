package com.cloudblog.content.controller.admin;

import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin/content")
public class ContentManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取文章列表
     * @param po
     * @return
     * @throws IOException
     */
    @PostMapping("/postList")
    public AjaxResult postList(@RequestBody ContentListManagePo po) throws IOException {
        return managerService.getContentList(po, ContentType.POST);
    }

    /**
     * 获取动态列表
     */
    @PostMapping("/shareList")
    public AjaxResult shareList(@RequestBody ContentListManagePo po) throws IOException {
        return managerService.getContentList(po, ContentType.SHARE);
    }

}
