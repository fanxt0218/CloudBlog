package com.cloudblog.content.controller.admin;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/redis")
public class RedisMangeController {

    @Autowired
    private ManagerService managerService;

    /**
     * 根据key 获取value
     */
    @GetMapping("/get")
    public AjaxResult get(String key) {
        return managerService.getRedisValue(key);
    }

    /**
     * 刷新缓存
     */
    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestParam(required = false)List<String> keys) {
        return managerService.refreshCache(keys);
    }
}
