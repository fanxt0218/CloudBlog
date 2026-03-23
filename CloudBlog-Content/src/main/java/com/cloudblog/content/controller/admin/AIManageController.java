package com.cloudblog.content.controller.admin;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/agent")
public class AIManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 查询RAG
     */
    @GetMapping("/getRagText")
    public AjaxResult getRagText() {
        return managerService.getRagText();
    }

    /**
     * 修改RAG
     */
    @PostMapping("/editRag")
    public AjaxResult editRagText(@RequestParam("file") MultipartFile file) {
        return managerService.editRagText(file);
    }
}
