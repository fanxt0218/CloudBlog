package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.DownloadResource;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Vo.IndexResourceVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceMapper extends BaseMapper<DownloadResource> {

    /**
     * 获取首页资源列表(兴趣推荐)
     * @param userId
     * @param categoryNames
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<IndexResourceVo> getResourceListWithInterest(
            @Param("userId") Long userId,
            @Param("categoryNames") List<String> categoryNames,
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i);

    /**
     * 获取首页资源列表(无兴趣推荐)
     * @param userId
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @param categoryNames
     * @return
     */
    List<IndexResourceVo> getResourceListWithNoInterest(
            @Param("userId") Long userId, Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i,
            @Param("categoryNames") List<String> categoryNames);

    /**
     * 搜索资源
     * @param keyword
     * @return
     */
    List<IndexResourceVo> queryResource(String keyword);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserInfo getUserInfo(Long userId);
}
