package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.PostAndShareInfo;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.pojo.Vo.ContentReviewVo;
import com.cloudblog.common.pojo.Vo.IndexShareVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ManagerMapper {

    /**
     * 获取内容审核列表
     * @param title
     * @param startTime
     * @param endTime
     * @param author
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<ContentReviewVo> getContentReviewList(
            Page<ContentReviewVo> page,
            @Param("title") String title,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("type") Integer type,
            @Param("author") String author,
            @Param("pageNum") Integer pageNum,
            @Param("pageSize") Integer pageSize
    );

    /**
     * 获取内容审核列表
     * @param type
     * @param id
     * @return
     */
    Integer getContentStatus(@Param("type") Integer type, @Param("id") Long id);

    /**
     * 内容审核
     * @param type
     * @param id
     */
    void contentReview(@Param("type") Integer type, @Param("id") Long id, @Param("status") Integer status);

    /**
     * 获取内容作者信息
     * @param type
     * @param id
     * @return
     */
    UserInfo getContentAuthorInfo(@Param("type") Integer type, @Param("id") Long id);

    /**
     * 获取内容信息
     * @param type
     * @param id
     * @return
     */
    PostAndShareInfo getContentInfo(@Param("type") Integer type, @Param("id") Long id);

    /**
     * 获取内容列表
     * @param po
     * @return
     */
    IPage<IndexShareVo> searchShareList(Page<IndexShareVo> page, @Param("po") ContentListManagePo po);
}
