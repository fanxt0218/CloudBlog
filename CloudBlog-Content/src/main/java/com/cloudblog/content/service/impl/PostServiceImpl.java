package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentStoreType;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.enums.PostType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Dto.PostDataInfo;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.pojo.Vo.*;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.FavoritesService;
import com.cloudblog.content.service.InterestService;
import com.cloudblog.content.service.PostService;
import com.cloudblog.content.service.ShareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    @Value("${file.resource.content.defaultCover}")
    private String defaultCoverPath;

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
        po.setFavoritesId(po.getFavoritesId() == null || po.getFavoritesId() == 0 ? favoritesService.getUserDefaultFavorites(po.getUserId()).getId() : po.getFavoritesId());
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
                String nextCursor = generateCursor(post,null);
                response.setNextCursor(nextCursor);
                // 总元素数
                Long totalCount = postMapper.getUserTotalCount(userId,PostType.POST.ordinal());
                response.setTotalElements(totalCount);
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
        if (data == null || (po.getTagId() != null && po.getTagId() != 0) || po.getUserId() == null) {
            withInterest = false;
        }
        // 默认文章
        if (po.getPostType() == null) {
            po.setPostType(PostType.POST.ordinal());
        }
        // 执行查询
        try {
            // 默认参数处理
            size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

            // 解析游标
            Map<String, Object> cursorMap = parseCursor(cursor);
            Long lastId = null;
            LocalDateTime lastCreateTime = null;
            double lastInterestScore = 0;

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
                Object interestScoreObj = cursorMap.get("interestScore");
                if (interestScoreObj != null) {
                    if (interestScoreObj instanceof Number) {
                        lastInterestScore = ((Number) interestScoreObj).doubleValue();
                    } else if (interestScoreObj instanceof String) {
                        lastInterestScore = Double.parseDouble((String) interestScoreObj);
                    }
                }
            }
            List<UserPostVo> posts;
            if (withInterest){
                // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
                // 兴趣推荐
                posts = postMapper.getPostListWithInterest(po.getUserId(), lastId, lastCreateTime, size + 1, po.getPostType(), lastInterestScore);
            }else {
                // 默认推荐
                posts = postMapper.getPostListWithNoInterest(po.getUserId(), lastId, lastCreateTime, size + 1, po.getTagId(), po.getPostType());

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
                String nextCursor = generateCursor(post, lastPost.getInterestScore());
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

    @Transactional
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

    @Override
    public List<IndexFocusArticleVo> getFocusPostList(Long lastTargetId, LocalDateTime lastCreateTime, int i, Long userId) {
        return postMapper.getFocusPostList(lastTargetId, lastCreateTime, i, userId);
    }

    @Transactional
    @Override
    public AjaxResult publish(PublishPostPo po) {
        Long postId;
        try {
            // 验证参数
            validatePostPublishParam(po);
            // 判断是否基于草稿发布
            if (po.getPostId() != null && po.getPostId() > 0) {
                // 执行更新
                publishDraft(po);
                return AjaxResult.success("发布成功");
            }
            // 插入文章内容表
            PostsContent postContent = new PostsContent();
            postContent.setContent(po.getContent());
            postContent.setContentType(ContentStoreType.HTML.ordinal());
            postMapper.insertContent(postContent);
            // 插入文章表
            Posts posts = new Posts();
            posts.setAuthorId(po.getUserId());
            posts.setTitle(po.getTitle());
            posts.setIntroduction(po.getIntro());
            posts.setImage(po.getCover());
            posts.setStatus(PostStatus.REVIEWING.getCode());
            posts.setContentId(postContent.getId());
            posts.setType(po.getType());
            posts.setPostType(po.getPostType());
            posts.setIsVip(po.getVip());
            posts.setCategoryId(po.getCategoryId());
            posts.setCreateTime(LocalDateTime.now());
            postMapper.insert(posts);
            postId = posts.getId();
            // 插入文章标签表
            interestService.addPostTag(po.getTagIds(), posts.getId());
            // TODO 加经验值
        } catch (Exception e) {
            log.error("文章发布失败：{}", e.getMessage());
            throw new CloudBlogException("文章发布失败: "+e.getMessage(), CommonError.INTERNAL_ERROR);
        }
        return AjaxResult.success("发布成功", postId);
    }

    @Override
    public AjaxResult getPost(Long postId, Long userId) {
        if (postId == null) {
            return AjaxResult.error("参数错误");
        }
        PostViewVo postViewVo = new PostViewVo();

        // 获取文章
        Posts posts = postMapper.selectById(postId);
        if (posts == null) {
            return AjaxResult.error("文章不存在");
        }
        BeanUtils.copyProperties(posts, postViewVo);
        postViewVo.setPostId(postId);

        // 查询分类信息
        if (posts.getCategoryId() != null) {
            Category category = postMapper.getPostCategoryInfo(posts.getCategoryId());
            postViewVo.setCategoryName(category.getCategoryName());
            postViewVo.setCategoryCover(category.getImage());
        }

        // 获取文章标签信息
        List<Tag> tagList = interestService.getPostTagInfo(postId);
        postViewVo.setTagList(
                tagList.stream()
                        .map(tag -> new PostViewVo.PostTagInfo(tag.getId(), tag.getTagName()))
                        .toList()
        );

        // 文章内容
        PostsContent content = postMapper.getPostContent(posts.getContentId());
        if (content == null) {
            return AjaxResult.error("文章内容不存在");
        }
        postViewVo.setStoreType(content.getContentType());
        postViewVo.setContent(content.getContent());
        // 统计文章数据
        PostDataInfo postDataInfo = statisticPostData(postId);
        postViewVo.setBrowseCount(postDataInfo.getBrowseCount());
        postViewVo.setLikeCount(postDataInfo.getLikeCount());
        postViewVo.setCollectCount(postDataInfo.getCollectCount());
        postViewVo.setCommentCount(postDataInfo.getCommentCount());

        // 判断是否点赞、收藏(登录的情况下)
        if (userId != null) {
            postViewVo.setLiked(postMapper.isPostLiked(postId, userId).compareTo(0L) > 0 ? 1 : 0);
            postViewVo.setCollected(postMapper.isPostCollected(postId, userId).compareTo(0L) > 0 ? 1 : 0);
        } else {
            postViewVo.setLiked(0);
            postViewVo.setCollected(0);
        }

        return AjaxResult.success(postViewVo);
    }

    @Override
    public AjaxResult getBrowseTopPostList(Integer postType) {
        List<PostWithBrowseCountVo> posts = postMapper.getBrowseTopPostList(postType);
        return AjaxResult.success(posts);
    }

    @Override
    public AjaxResult saveDraft(PublishPostPo po) {
        if (po.getPostId() != null && po.getPostId() > 0) {
            // 更新草稿
            PostsContent postContent = new PostsContent();
            postContent.setId(po.getPostId());
            postContent.setContent(po.getContent());
            postContent.setContentType(ContentStoreType.HTML.ordinal());
            postMapper.updateContent(postContent);

            Posts posts = new Posts();
            posts.setAuthorId(po.getUserId());
            posts.setTitle(po.getTitle().isEmpty()? "无标题" : po.getTitle());
            posts.setIntroduction(po.getIntro());
            posts.setStatus(PostStatus.DRAFT.getCode());
            posts.setContentId(postContent.getId());
            posts.setUpdateTime(LocalDateTime.now());
            postMapper.update(posts, new LambdaUpdateWrapper<Posts>().eq(Posts::getId, po.getPostId()));
            Long postId = po.getPostId();
            return AjaxResult.success("保存成功", postId);
        }
        // 插入文章内容表
        PostsContent postContent = new PostsContent();
        postContent.setContent(po.getContent());
        postContent.setContentType(ContentStoreType.HTML.ordinal());
        postMapper.insertContent(postContent);
        // 插入文章表
        Posts posts = new Posts();
        posts.setAuthorId(po.getUserId());
        posts.setTitle(po.getTitle().isEmpty()? "无标题" : po.getTitle());
        posts.setIntroduction(po.getIntro());
        posts.setStatus(PostStatus.DRAFT.getCode());
        posts.setContentId(postContent.getId());
        posts.setCreateTime(LocalDateTime.now());
        postMapper.insert(posts);
        Long postId = posts.getId();
        return AjaxResult.success("保存成功", postId);
    }

    @Override
    public AjaxResult getUserDraftList(Long userId) {
        if (userId == null) {
            return AjaxResult.error("参数错误");
        }
        List<Posts> posts = postMapper.selectList(new LambdaQueryWrapper<Posts>()
                .eq(Posts::getAuthorId, userId)
                .eq(Posts::getStatus, PostStatus.DRAFT.getCode())
        );
        List<DraftVo> res = posts.stream().map(p -> new DraftVo(p.getId(), p.getTitle(), p.getUpdateTime())).toList();
        return AjaxResult.success(res);
    }

    /**
     * 发布文章（基于草稿）
     */
    @Transactional
    protected void publishDraft(PublishPostPo po) {
        try {
            // 获取草稿信息
            Posts draft = postMapper.selectById(po.getPostId());
            // 更新文章内容表
            PostsContent postContent = new PostsContent();
            postContent.setId(draft.getContentId());
            postContent.setContent(po.getContent());
            postContent.setContentType(ContentStoreType.HTML.ordinal());
            postMapper.updateContent(postContent);
            // 更新文章表
            Posts posts = new Posts();
            posts.setAuthorId(po.getUserId());
            posts.setTitle(po.getTitle());
            posts.setIntroduction(po.getIntro());
            posts.setImage(po.getCover());
            posts.setStatus(PostStatus.REVIEWING.getCode());
            posts.setContentId(postContent.getId());
            posts.setType(po.getType());
            posts.setPostType(po.getPostType());
            posts.setIsVip(po.getVip());
            posts.setCategoryId(po.getCategoryId());
            posts.setCreateTime(LocalDateTime.now());
            postMapper.update(posts, new LambdaUpdateWrapper<Posts>().eq(Posts::getId, draft.getId()));
            // 插入文章标签表
            interestService.addPostTag(po.getTagIds(), po.getPostId());
        } catch (Exception e) {
            log.error("文章发布失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证发布文章参数
     * @param po 发布文章参数
     */
    private void validatePostPublishParam(PublishPostPo po) {
        String errMsg = "";
        boolean goOn = true;
        if (po.getUserId() == null) {
            errMsg = "用户ID不能为空";
            goOn = false;
        }
        if (goOn && (po.getTitle() == null || po.getTitle().isEmpty())) {
            errMsg = "标题不能为空";
            goOn = false;
        }
        if (goOn && (po.getContent() == null || po.getContent().isEmpty())) {
            errMsg = "内容不能为空";
            goOn = false;
        }
        if (goOn && (po.getCover() == null || po.getCover().isEmpty())) {
            po.setCover("/profile" + defaultCoverPath + "/defaultCover.png");
        }
        if (goOn && (po.getTagIds() == null || po.getTagIds().isEmpty())) {
            errMsg = "文章标签不能为空";
            goOn = false;
        }
        if (goOn && po.getCategoryId() == null) {
            errMsg = "文章分类不能为空";
            goOn = false;
        }
        if (goOn && po.getType() == null) {
            errMsg = "可见范围不能为空";
            goOn = false;
        }
        if (goOn && po.getPostType() == null) {
            errMsg = "文章类型不能为空";
            goOn = false;
        }
        if (!goOn) {
            CloudBlogException.cast(errMsg);
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
     * @param post 当前文章
     * @return 游标字符串
     */
    private String generateCursor(Posts post, Double interestScore) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("id", post.getId());
            cursorMap.put("createTime", post.getCreateTime().toString());
            if (interestScore != null) {
                cursorMap.put("interestScore", interestScore);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    public PostDataInfo statisticPostData(Long postId) {
        return postMapper.CalculatePostData(postId);
    }

}
