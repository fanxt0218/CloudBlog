package com.cloudblog.content.controller.admin;

import com.cloudblog.common.pojo.DoMain.WorkOrder;
import com.cloudblog.common.pojo.Po.WorkOrderListPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.ManagerMapper;
import com.cloudblog.content.service.ManagerService;
import com.cloudblog.content.service.WorkOrderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workOrder")
public class WorkOrderManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取工单列表
     */
    @PostMapping("/getWorkOrderList")
    public AjaxResult getWorkOrderList(@RequestBody WorkOrderListPo po) {
        return managerService.getWorkOrderList(po);
    }

    /**
     * 获取工单详情
     */
    @PostMapping("/getWorkOrderDetail")
    public AjaxResult getWorkOrderDetail(@RequestBody WorkOrder workOrder) {
        return managerService.getWorkOrderDetail(workOrder);
    }

    /**
     * 处理工单
     */
    @PostMapping("/handleWorkOrder")
    public AjaxResult handleWorkOrder(@RequestBody WorkOrder workOrder) {
        return managerService.handleWorkOrder(workOrder);
    }
}
