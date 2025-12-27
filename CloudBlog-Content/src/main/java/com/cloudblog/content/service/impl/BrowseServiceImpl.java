package com.cloudblog.content.service.impl;

import com.cloudblog.content.mapper.BrowseMapper;
import com.cloudblog.content.service.BrowseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrowseServiceImpl implements BrowseService {

    @Autowired
    private BrowseMapper browseMapper;

    @Override
    public Long calculateBrowseCount(Long contentId, Integer type) {
        return browseMapper.calculateBrowseCount(contentId, type);
    }
}
