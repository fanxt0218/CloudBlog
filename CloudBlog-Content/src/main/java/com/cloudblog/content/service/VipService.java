package com.cloudblog.content.service;

import com.cloudblog.common.result.AjaxResult;

public interface VipService {

    AjaxResult getUserVipInfo(Long userId);

    AjaxResult openVip(Long userId);
}
