package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.pojo.Dto.ESPost;
import com.cloudblog.common.pojo.Dto.PostAndShareInfo;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.pojo.Po.UserListPo;
import com.cloudblog.common.pojo.Po.WorkOrderListPo;
import com.cloudblog.common.pojo.Vo.*;
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

    /**
     * 获取标签分类信息
     * @param classId
     * @return
     */
    TagClass getTagClassInfo(Integer classId);

    /**
     * 获取标签信息
     * @param tag
     * @return
     */
    void editTag(@Param("tag") Tag tag);

    /**
     *  获取标签信息
     * @param classId
     * @return
     */
    List<Tag> getTagByTagClass(Integer classId);

    /**
     * 编辑标签分类信息
     * @param tagClass
     * @return
     */
    void editTagClass(@Param("tagClass") TagClass tagClass);

    /**
     * 标签
     * @param topic
     */
    void editTopic(@Param("topic") Topic topic);

    /**
     * 获取用户列表
     * @param po
     * @return
     */
    IPage<UserDetailVo> getUserList(Page<UserDetailVo> page, @Param("po") UserListPo po);

    /**
     * 重置密码
     * @param targetId
     * @param defaultHashPass
     */
    void resetPassword(@Param("targetId") Long targetId, @Param("defaultHashPass") String defaultHashPass);

    /**
     * 删除用户
     * @param targetId
     * @param status
     */
    void updateUserStatus(@Param("targetId") Long targetId, @Param("status") Integer status);

    /**
     * 获取标签信息
     * @param tagName
     * @return
     */
    List<Tag> getTagByTagName(String tagName);

    /**
     * 添加标签
     * @param tag
     */
    void addTag(@Param("tag") Tag tag);

    /**
     * 添加标签分类
     * @param className
     */
    List<TagClass> getTagClassByClassName(String className);

    /**
     * 添加标签分类
     * @param tagclass
     */
    void addTagClass(@Param("tagClass") TagClass tagclass);

    /**
     * 添加标签分类
     * @param topicName
     */
    List<Topic> getTopicByTopicName(String topicName);

    /**
     * 添加标签分类
     * @param topic
     */
    void addTopic(@Param("topic") Topic topic);

    /**
     * 编辑用户信息
     * @param userInfo
     */
    void editUser(@Param("userInfo") UserInfo userInfo);

    /**
     * 获取工单列表
     * @param page
     * @param po
     * @return
     */
    IPage<WorkOrderVo> getWorkOrderList(Page<WorkOrderVo> page, @Param("po") WorkOrderListPo po);

    /**
     * 处理工单
     * @param workOrder
     */
    void handleWorkOrder(@Param("workOrder") WorkOrder workOrder);

    /**
     * 获取文章总数
     * @return
     */
    Long getTotalPostsCount();

    /**
     * 获取分享总数
     * @return
     */
    Long getTotalSharesCount();

    /**
     * 获取用户总数
     * @return
     */
    Long getTotalUserCount();

    /**
     * 获取热门 TOP10 文章 (综合评分：浏览*0.1 + 点赞*0.3 + 收藏*0.2 + 评论*0.4)
     * @param limit 返回数量
     * @return
     */
    List<HotArticleVo> getHotArticleTop10(@Param("limit") Integer limit);

    /**
     * 获取文章信息
     * @param id
     * @return
     */
    Posts getPost(Long id);

    /**
     * 获取文章信息
     * @param id
     * @return
     */
    ESPost getESPostInfo(Long id);
}
