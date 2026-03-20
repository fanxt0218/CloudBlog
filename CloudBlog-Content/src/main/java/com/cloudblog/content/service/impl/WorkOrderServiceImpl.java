package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.WorkOrderType;
import com.cloudblog.common.pojo.DoMain.WorkOrder;
import com.cloudblog.common.pojo.Po.ReportPo;
import com.cloudblog.common.pojo.Vo.WorkOrderVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.UserContext;
import com.cloudblog.common.utils.WorkOrderUtil;
import com.cloudblog.content.mapper.WorkOrderMapper;
import com.cloudblog.content.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Override
    public AjaxResult report(ReportPo po) {
        Long userId = UserContext.getUser();
        WorkOrder workOrder = new WorkOrder();
        workOrder.setUserId(userId);
        workOrder.setTargetId(po.getTargetId());
        workOrder.setTargetType(po.getTargetType());
        workOrder.setOrderType(WorkOrderType.CONTENT_REPORT.getCode()); // 目前只有内容举报
        workOrder.setReason(po.getReason());
        if (po.getFilePath() != null && !po.getFilePath().isEmpty()) {
            workOrder.setFilePath(po.getFilePath());
        }
        workOrder.setCreateTime(LocalDateTime.now());
        String orderId = getWorkOrderTypeName(WorkOrderType.CONTENT_REPORT.getCode()).isEmpty()?
                WorkOrderUtil.generateWorkOrderNumber() :
                WorkOrderUtil.generateWorkOrderNumberWithBusinessType(getWorkOrderTypeName(workOrder.getOrderType()));
        workOrder.setOrderId(orderId);
        workOrderMapper.insert(workOrder);
        return AjaxResult.success("提交成功", orderId);
    }

    @Override
    public AjaxResult query(Long userId, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize <= 0 ? 10 : pageSize;

        Page<WorkOrder> page = new Page<>(pageNum, pageSize);
        IPage<WorkOrderVo> list = workOrderMapper.getWorkOrderList(page, userId);
        return AjaxResult.success(list);
    }

    /**
     * 根据工单类型返回标识
     */
    private String getWorkOrderTypeName(Integer workOrderType) {
        return switch (workOrderType) {
            case 0 -> "CR"; // 内容举报
            case 1 -> "BU"; // BUG
            case 2 -> "RE"; // 建议
            default -> "";
        };
    }
}
