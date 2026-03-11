package com.cloudblog.content.controller.admin;

import com.cloudblog.common.pojo.Po.UserListPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class UserManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取用户列表
     */
    @PostMapping("/list")
    public AjaxResult getUserList(@RequestBody UserListPo po) {
        return managerService.getUserList(po);
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public AjaxResult resetPassword(@RequestParam Long targetId) {
        return managerService.resetPassword(targetId);
    }

    /**
     * 修改用户账号状态（0恢复，1删除）
     */
    @PostMapping("/updateStatus")
    public AjaxResult deleteUser(
            @RequestParam Long targetId,
            @RequestParam Integer status
            ) {
        return managerService.updateUserStatus(targetId, status);
    }
}
