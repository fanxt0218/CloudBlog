package com.cloudblog.content.controller.admin;

import com.cloudblog.common.pojo.Po.ReviewOpinionPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/content/review")
public class ContentReviewController {

    @Autowired
    private ManagerService managerService;

    /**
     * 内容审核-获取审核列表
     */
    @GetMapping("/list")
    public AjaxResult ContentReviewList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
            ) {
        return managerService.ContentReviewList(title, startTime, endTime, type, author, pageNum, pageSize);
    }

    /**
     * 审核
     */
    @PostMapping("/{type}/{id}")
    public AjaxResult ContentReview(
            @PathVariable Integer type,
            @PathVariable Long id,
            @RequestBody ReviewOpinionPo po
            ) {
        return managerService.ContentReview(type, id, po);
    }

}
