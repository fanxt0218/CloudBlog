package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.ReportPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @PostMapping("/report")
    public AjaxResult report(@RequestBody ReportPo po) {
        return workOrderService.report(po);
    }
}
