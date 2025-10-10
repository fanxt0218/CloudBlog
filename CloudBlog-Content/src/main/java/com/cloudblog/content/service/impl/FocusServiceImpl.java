package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;
import com.cloudblog.content.mapper.FocusMapper;
import com.cloudblog.content.service.FocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FocusServiceImpl implements FocusService {

    @Autowired
    private FocusMapper focusMapper;

    @Override
    public List<UserFocusListVo> getUserFocusList(Long userId) {
        return focusMapper.getUserFocusList(userId);
    }

    @Override
    public List<UserFanListVo> getUserFanList(Long userId) {
        return focusMapper.getUserFanList(userId);
    }
}
