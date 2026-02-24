package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Po.ReportPo;
import com.cloudblog.common.result.AjaxResult;

public interface WorkOrderService {

    AjaxResult report(ReportPo po);
}
