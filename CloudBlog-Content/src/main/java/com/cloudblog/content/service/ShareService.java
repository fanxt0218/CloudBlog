package com.cloudblog.content.service;

import com.cloudblog.common.result.AjaxResult;

public interface ShareService {

    AjaxResult getUserShareList(Long userId, String cursor, Integer size, String sortBy, String tag);
}
