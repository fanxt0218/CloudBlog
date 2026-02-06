package com.cloudblog.content.mapper;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SearchMapper {

    /**
     * 获取用户搜索历史
     * @param userId
     * @return
     */
    List<UserSearchHistory> getUserSearchHistory(Long userId);

    /**
     * 添加用户搜索记录
     * @param po
     * @return
     */
    int addSearchRecord(@Param("po") UserSearchHistory po);
}
