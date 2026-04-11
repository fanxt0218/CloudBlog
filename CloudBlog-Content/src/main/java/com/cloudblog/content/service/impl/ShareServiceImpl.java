package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.DoMain.Share;
import com.cloudblog.common.pojo.DoMain.Topic;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Dto.UserSimpleInfo;
import com.cloudblog.common.pojo.Po.DeletePostPo;
import com.cloudblog.common.pojo.Po.EditSharePo;
import com.cloudblog.common.pojo.Po.PublishSharePo;
import com.cloudblog.common.pojo.Vo.*;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.config.ContentStartupConfig;
import com.cloudblog.content.mapper.ShareMapper;
import com.cloudblog.content.service.BrowseService;
import com.cloudblog.content.service.CommentService;
import com.cloudblog.content.service.LikeService;
import com.cloudblog.content.service.ShareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private BrowseService browseService;
    @Autowired
    @Lazy
    private LikeService likeService;
    @Autowired
    private CommentService commentService;

    @Autowired
    @Lazy
    private ContentStartupConfig contentStartupConfig;

    @Override
    public AjaxResult getUserShareList(Long userId, String cursor, Integer size, String sortBy, String tag) {
        try {
            // 默认参数处理
            size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

            // 解析游标
            Map<String, Object> cursorMap = parseCursor(cursor);
            Long lastId = null;
            LocalDateTime lastCreateTime = null;

            if (cursorMap != null) {
                lastId = ((Number) cursorMap.get("id")).longValue();
                Object createTimeObj = cursorMap.get("createTime");
                if (createTimeObj != null) {
                    if (createTimeObj instanceof String) {
                        lastCreateTime = LocalDateTime.parse((String) createTimeObj);
                    } else if (createTimeObj instanceof LocalDateTime) {
                        lastCreateTime = (LocalDateTime) createTimeObj;
                    }
                }
            }

            // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
            List<UserShareVo> shares = shareMapper.getUserPostList(userId, lastId, lastCreateTime, size + 1);

            // 构建PageResponse返回结果
            PageResponse<UserShareVo> response = new PageResponse<>();
            response.setPageSize(size);

            // 判断是否还有更多数据
            boolean hasNext = shares.size() > size;
            response.setHasNext(hasNext);

            // 设置实际返回的数据列表
            if (hasNext) {
                // 移除多查的一个元素
                response.setContent(shares.subList(0, size));
                // 生成下一个游标
                UserShareVo lastShare = shares.get(size - 1);
                Share share = new Share();
                BeanUtils.copyProperties(lastShare, share);
                String nextCursor = generateCursor(share);
                response.setNextCursor(nextCursor);
            } else {
                response.setContent(shares);
            }

            return AjaxResult.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取用户动态列表失败: " + e.getMessage());
        }
    }

    @Override
    public void addShareBrowseCount(Long postId, Long userId) {
        shareMapper.addShareBrowseCount(postId, userId);
    }

    @Override
    public AjaxResult getIndexShareList(String cursor, Integer size, Integer topicId) {
        try {
            // 默认参数处理
            size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

            topicId = (topicId == null || topicId <= 0) ? null : topicId;

            // 解析游标
            Map<String, Object> cursorMap = parseCursor(cursor);
            Long lastId = null;
            LocalDateTime lastCreateTime = null;

            if (cursorMap != null) {
                lastId = ((Number) cursorMap.get("id")).longValue();
                Object createTimeObj = cursorMap.get("createTime");
                if (createTimeObj != null) {
                    if (createTimeObj instanceof String) {
                        lastCreateTime = LocalDateTime.parse((String) createTimeObj);
                    } else if (createTimeObj instanceof LocalDateTime) {
                        lastCreateTime = (LocalDateTime) createTimeObj;
                    }
                }
            }

            // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
            List<IndexShareVo> shares = shareMapper.getIndexPostList(lastId, lastCreateTime, size + 1, topicId);

            // 构建PageResponse返回结果
            PageResponse<IndexShareVo> response = new PageResponse<>();
            response.setPageSize(size);

            // 判断是否还有更多数据
            boolean hasNext = shares.size() > size;
            response.setHasNext(hasNext);

            // 设置实际返回的数据列表
            if (hasNext) {
                // 移除多查的一个元素
                response.setContent(shares.subList(0, size));
                // 生成下一个游标
                IndexShareVo lastShare = shares.get(size - 1);
                Share share = new Share();
                BeanUtils.copyProperties(lastShare, share);
                String nextCursor = generateCursor(share);
                response.setNextCursor(nextCursor);
            } else {
                response.setContent(shares);
            }

            return AjaxResult.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取首页动态列表失败: " + e.getMessage());
        }
    }

    @Override
    public AjaxResult getTopicList(String title, Integer pageNum, Integer pageSize) {
        boolean isSearch = pageNum != null || pageSize != null;
        pageNum = (pageNum == null || pageNum <= 0) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        Page<IndexTopicVo> page = new Page<>(pageNum, pageSize);
        IPage<IndexTopicVo> topicList = shareMapper.getTopicList(page, title);
        if (isSearch) {
            return AjaxResult.success(topicList);
        } else {
            return AjaxResult.success(topicList.getRecords());
        }
    }

    @Override
    public List<IndexFocusArticleVo> getFocusShareList(Long lastTargetId, LocalDateTime lastCreateTime, int i, Long userId) {
        return shareMapper.getFocusShareList(lastTargetId, lastCreateTime, i, userId);
    }

    @Override
    public AjaxResult getPublishPageTopicList() {
        List<IndexTopicVo> topicList = (List<IndexTopicVo>) this.getTopicList(null, null, null).get("data");

        List<PublishPageTopicListVo> publishPageTopicListVos = new ArrayList<>();
        for (IndexTopicVo topic : topicList) {
            PublishPageTopicListVo publishPageTopicVo = shareMapper.getPublishPageTopicList(topic.getId());
            BeanUtils.copyProperties(topic, publishPageTopicVo);
            publishPageTopicListVos.add(publishPageTopicVo);
        }
        return AjaxResult.success(publishPageTopicListVos);
    }

    @Override
    public AjaxResult publish(PublishSharePo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("参数错误");
        }
        try {
            Share share = new Share();
            share.setAuthorId(po.getUserId());
            share.setContent(po.getContent());
            share.setTopicId(po.getTopicId());
            share.setCreateTime(LocalDateTime.now());
            share.setStatus(1);
            // 生成简略描述
            String brief = generateBrief(share.getContent());
            share.setBrief(brief);
            //TODO 目前只支持上传一张图片
            if (po.getImageUrls() != null && !po.getImageUrls().isEmpty()) {
                share.setImage(po.getImageUrls().get(0));
            }
            if (po.getVideoUrls() != null && !po.getVideoUrls().isEmpty()) {
                share.setVideo(po.getVideoUrls().get(0));
            }
            shareMapper.insert(share);
        } catch (Exception e) {

            return AjaxResult.error("发布失败");
        }
        return AjaxResult.success("发布成功");
    }

    @Override
    public AjaxResult getShare(Long shareId, Long userId) {
        ShareViewVo shareViewVo = new ShareViewVo();
        // 动态信息
        Share share = shareMapper.selectOne(new LambdaQueryWrapper<Share>().eq(Share::getId, shareId));
        // 用户信息
        UserInfo authorInfo = shareMapper.getAuthorInfo(share.getAuthorId());
        // 计算动态各项参数
        calculateShareParameters(share, shareViewVo);
        shareViewVo.setId(share.getId());
        shareViewVo.setAuthorId(share.getAuthorId());
        shareViewVo.setContent(share.getContent());
        shareViewVo.setTopicId(share.getTopicId());
        shareViewVo.setCreateTime(share.getCreateTime());
        shareViewVo.setImageUrl(share.getImage());
        shareViewVo.setVideoUrl(share.getVideo());

        shareViewVo.setUserName(authorInfo.getUserName());
        shareViewVo.setUserImage(authorInfo.getImage());

        // 判断用户是否已点赞
        if (userId != null) {
            shareViewVo.setLike(handleLike(shareId, userId, ContentType.SHARE));
        }
        return AjaxResult.success(shareViewVo);
    }

    @Override
    public AjaxResult delete(DeletePostPo po) {
        // 检查文章作者
        if (!shareMapper.selectById(po.getPostId()).getAuthorId().equals(po.getUserId())) {
            return AjaxResult.error("您没有权限删除该动态");
        }
        int update = shareMapper.update(new LambdaUpdateWrapper<>(Share.class)
                .eq(Share::getId, po.getPostId())
                .set(Share::getStatus, PostStatus.DELETED.getCode())
        );
        return update > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");

    }

    @Override
    public AjaxResult edit(EditSharePo po) {
        if (po.getUserId() == null || po.getShareId() == null) {
            return AjaxResult.error("参数错误");
        }
        try {
            Share share = new Share();
            share.setAuthorId(po.getUserId());
            share.setContent(po.getContent());
            share.setTopicId(po.getTopicId());
            share.setCreateTime(LocalDateTime.now());
            share.setStatus(1);
            // 生成简略描述
            String brief = generateBrief(share.getContent());
            share.setBrief(brief);
            //TODO 目前只支持上传一张图片
            if (po.getImageUrls() != null && !po.getImageUrls().isEmpty()) {
                share.setImage(po.getImageUrls().get(0));
            }
            if (po.getVideoUrls() != null && !po.getVideoUrls().isEmpty()) {
                share.setVideo(po.getVideoUrls().get(0));
            }
            shareMapper.update(share, new LambdaUpdateWrapper<>(Share.class).eq(Share::getId, po.getShareId()));
        } catch (Exception e) {
            return AjaxResult.error("发布失败");
        }
        return AjaxResult.success("发布成功");
    }

    /**
     * 处理点赞
     * @param shareId
     * @param userId
     * @param contentType
     * @return
     */
    private boolean handleLike(Long shareId, Long userId, ContentType contentType) {
        List<UserSimpleInfo> userInfo =  shareMapper.getShareLikeUserInfo(shareId, contentType);
        if (userInfo != null && !userInfo.isEmpty()) {
            return userInfo.stream().anyMatch(user -> user.getUserId().equals(userId));
        }
        return false;
    }

    /**
     * 生成简略描述
     * @param content
     * @return
     */
    private String generateBrief(String content) {
        if (content.length() > 60) {
            return content.substring(0, 60) + "...";
        } else {
            return content;
        }
    }

    /**
     * 解析游标字符串
     * @param cursor 游标字符串
     * @return 解析后的游标数据
     */
    private Map<String, Object> parseCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(decoded, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成游标字符串
     * @param share 当前动态
     * @return 游标字符串
     */
    private String generateCursor(Share share) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("id", share.getId());
            cursorMap.put("createTime", share.getCreateTime().toString());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    private void calculateShareParameters(Share share, ShareViewVo shareViewVo) {
        Long shareId = share.getId();
        // 计算浏览数
        Long browseCount = browseService.calculateBrowseCount(shareId, ContentType.SHARE.ordinal());
        // 计算点赞数
        Long likeCount = likeService.calculateLikeCount(shareId, ContentType.SHARE.ordinal());
        // 计算评论数
        Long commentCount = commentService.calculateCommentCount(shareId, ContentType.SHARE.ordinal());

        shareViewVo.setBrowseCount(browseCount);
        shareViewVo.setLikeCount(likeCount);
        shareViewVo.setCommentCount(commentCount);
    }
}
