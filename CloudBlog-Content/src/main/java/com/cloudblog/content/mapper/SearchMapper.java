package com.cloudblog.content.mapper;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import com.cloudblog.common.pojo.Vo.SearchUserVo;
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

    /**
     * 搜索用户
     * @param keyword
     * @return
     */
    List<SearchUserVo> searchUser(String keyword);

    /**
     * 删除用户搜索记录
     * @param ids
     * @return
     */
    int deleteSearchRecord(@Param("ids") List<Long> ids);

    /**
     * 清空用户搜索记录
     * @param userId
     * @return
     */
    int clearUserSearchRecord(Long userId);
}
