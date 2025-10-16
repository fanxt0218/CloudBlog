package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.enums.PostType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.DoMain.PostTag;
import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.pojo.Vo.UserBrowseListVo;
import com.cloudblog.common.pojo.Vo.UserCollectListVo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.FavoritesService;
import com.cloudblog.content.service.InterestService;
import com.cloudblog.content.service.PostService;
import com.cloudblog.content.service.ShareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private FavoritesService favoritesService;
    @Autowired
    private InterestService interestService;
    @Autowired
    private ShareService shareService;

    @Override
    public IPage<UserLikeListVo> getUserLikeList(UserLikeListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();
        Page<UserLikeListVo> page = new Page<>(pageNum, pageSize);
        return postMapper.getUserLikeList(page,po.getUserId(), po.getBeginTime(), po.getEndTime(), ContentType.POST.ordinal());
    }

    @Override
    public IPage<UserCollectListVo> getUserCollectList(UserCollectListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();
        // 提前处理情况
        po.setPostName(po.getPostName() == null || po.getPostName().trim().isEmpty() ? null : po.getPostName());
        po.setFavoritesId(po.getFavoritesId() == null ? favoritesService.getUserDefaultFavorites(po.getUserId()).getId() : po.getFavoritesId());
        Page<UserCollectListVo> page = new Page<>(pageNum, pageSize);
        return postMapper.getUserCollectList(page,po.getUserId(),po.getFavoritesId(),po.getPostName());
    }

    @Override
    public IPage<UserBrowseListVo> getUserBrowseHistory(UserBrowseListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();
        Page<UserBrowseListVo> page = new Page<>(pageNum, pageSize);
        return postMapper.getUserBrowseHistory(page,po.getUserId(),po.getBeginTime(),po.getEndTime(),ContentType.POST.ordinal());
    }

    @Override
    public AjaxResult getUserPostList(Long userId, String cursor, Integer size, String sortBy, String tag) {
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
            List<UserPostVo> posts = postMapper.getUserPostList(userId, lastId, lastCreateTime, size + 1);

            // 构建PageResponse返回结果
            PageResponse<UserPostVo> response = new PageResponse<>();
            response.setPageSize(size);

            // 判断是否还有更多数据
            boolean hasNext = posts.size() > size;
            response.setHasNext(hasNext);

            // 设置实际返回的数据列表
            if (hasNext) {
                // 移除多查的一个元素
                response.setContent(posts.subList(0, size));
                // 生成下一个游标
                UserPostVo lastPost = posts.get(size - 1);
                Posts post = new Posts();
                BeanUtils.copyProperties(lastPost, post);
                String nextCursor = generateCursor(post);
                response.setNextCursor(nextCursor);
            } else {
                response.setContent(posts);
            }

            return AjaxResult.success(response);
        } catch (Exception e) {
            throw new CloudBlogException("获取用户文章列表失败", CommonError.INTERNAL_ERROR);
        }
    }

    @Override
    public AjaxResult getIndexPostList(PostPo po, String cursor, Integer size, String sortBy, String tag) {
        // 判断是否有用户id（是否登录），以此去配置推荐算法
        boolean withInterest = true;
        Object data = interestService.getInterestInfo(po.getUserId()).get("data");
        // 无兴趣/选择了tag/未登录=默认推荐
        if (data == null || po.getTagId() != null || po.getUserId() == null) {
            withInterest = false;
        }
        // 默认文章
        if (po.getPostTye() == null) {
            po.setPostTye(PostType.POST.ordinal());
        }
        // 执行查询
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
            List<UserPostVo> posts;
            if (withInterest){
                // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
                // 兴趣推荐
                posts = postMapper.getPostListWithInterest(po.getUserId(), lastId, lastCreateTime, size + 1, po.getPostTye());
            }else {
                // 默认推荐
                posts = postMapper.getPostListWithNoInterest(po.getUserId(), lastId, lastCreateTime, size + 1, po.getTagId(), po.getPostTye());

            }
            // 构建PageResponse返回结果
            PageResponse<UserPostVo> response = new PageResponse<>();
            response.setPageSize(size);

            // 判断是否还有更多数据
            boolean hasNext = posts.size() > size;
            response.setHasNext(hasNext);

            // 设置实际返回的数据列表
            if (hasNext) {
                // 移除多查的一个元素
                response.setContent(posts.subList(0, size));
                // 生成下一个游标
                UserPostVo lastPost = posts.get(size - 1);
                Posts post = new Posts();
                BeanUtils.copyProperties(lastPost, post);
                String nextCursor = generateCursor(post);
                response.setNextCursor(nextCursor);
            } else {
                response.setContent(posts);
            }

            return AjaxResult.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudBlogException("获取用户文章列表失败", CommonError.INTERNAL_ERROR);
        }
    }

    @Override
    public AjaxResult addBrowseCount(AddBrowseCountPo po) {
        if (po.getPostId() == null || po.getUserId() == null) {
            return AjaxResult.error("参数错误");
        }
        // 增加浏览量
        if (po.getContentType() == ContentType.POST.ordinal()) {
            // 文章
            postMapper.addPostBrowseCount(po.getPostId(), po.getUserId());
        } else if (po.getContentType() == ContentType.SHARE.ordinal()) {
            // 动态
            shareService.addShareBrowseCount(po.getPostId(), po.getUserId());
        }

        //只有文章才增加标签的权重
        if (po.getContentType().equals(ContentType.POST.ordinal())) {
            // 获取文章标签
            List<PostTag> tags = postMapper.getPostTagByPostId(po.getPostId());
            if (tags == null || tags.isEmpty()) {
                return AjaxResult.success();
            }
//            System.out.println("文章标签"+ tags);
            // 获取用户兴趣
            List<UserInterest> interests = postMapper.getUserInterest(po.getUserId());
            if (interests == null || interests.isEmpty()) {
                // 如果用户没有兴趣，则不进行该操作，后续可以添加隐式兴趣
                return AjaxResult.success();
            }
//            System.out.println("用户兴趣"+ interests);
            List<Integer> tagIds = tags.stream().map(PostTag::getTagId).toList();
            List<UserInterest> upgrades = new ArrayList<>();
            interests.forEach(interest -> {
                if (tagIds.contains(interest.getTagId())) {
                    // 先默认加0.1
                    interest.setWeight(interest.getWeight().add(new BigDecimal("0.1")));
                    upgrades.add(interest);
                }
            });
//            System.out.println("用户兴趣升级"+ upgrades);
            // 批量更新用户兴趣指数
            interestService.upgradeUserInterest(upgrades);
        }
        return AjaxResult.success();
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
     * @param post 当前文章
     * @return 游标字符串
     */
    private String generateCursor(Posts post) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("id", post.getId());
            cursorMap.put("createTime", post.getCreateTime().toString());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

}
