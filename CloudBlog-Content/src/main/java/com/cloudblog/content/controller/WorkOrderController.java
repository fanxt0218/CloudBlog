package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.DoMain.WorkOrder;
import com.cloudblog.common.pojo.Po.ReportPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 举报
     */
    @PostMapping("/report")
    public AjaxResult report(@RequestBody ReportPo po) {
        return workOrderService.report(po);
    }

    /**
     * 查询个人工单
     */
    @GetMapping("/query")
    public AjaxResult query(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return workOrderService.query(userId, pageNum, pageSize);
    }

    /**
     * 提交工单
     */
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody WorkOrder workOrder) {
        return workOrderService.submit(workOrder);
    }
}
