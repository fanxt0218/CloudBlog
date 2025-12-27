package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Browse;
import org.apache.ibatis.annotations.Param;

public interface BrowseMapper extends BaseMapper<Browse> {

    /**
     * 计算浏览量
     * @param contentId
     * @param type
     * @return
     */
    Long calculateBrowseCount(@Param("contentId") Long contentId, @Param("type") Integer type);
}
