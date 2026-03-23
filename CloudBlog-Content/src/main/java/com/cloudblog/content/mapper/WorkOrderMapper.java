package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.User;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.DoMain.WorkOrder;
import com.cloudblog.common.pojo.Vo.WorkOrderVo;
import org.apache.ibatis.annotations.Param;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 获取工单列表
     * @param page
     * @param userId
     * @return
     */
    IPage<WorkOrderVo> getWorkOrderList(Page<WorkOrder> page, @Param("userId") Long userId);

    /**
     * 根据手机号获取用户信息
     * @param phone
     * @return
     */
    User getUserInfoByPhone(String phone);
}
