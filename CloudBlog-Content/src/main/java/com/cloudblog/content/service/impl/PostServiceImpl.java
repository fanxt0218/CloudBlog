package com.cloudblog.content.service.impl;

import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.config.ESConfig;
import com.cloudblog.common.enums.ContentStoreType;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.enums.PostType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.pojo.Dto.CheckReport;
import com.cloudblog.common.pojo.Dto.ESPost;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Dto.PostDataInfo;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.pojo.Vo.*;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.ContentComplianceChecker;
import com.cloudblog.common.utils.ESUtil;
import com.cloudblog.common.utils.HtmlUtil;
import com.cloudblog.content.config.ContentStartupConfig;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.FavoritesService;
import com.cloudblog.content.service.InterestService;
import com.cloudblog.content.service.PostService;
import com.cloudblog.content.service.ShareService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Qualifier("exportTaskExecutor")
    private ThreadPoolTaskExecutor exportTaskExecutor;
    @Autowired
    private ContentComplianceChecker complianceChecker;


    @Autowired
    private ContentStartupConfig contentStartupConfig;

    @Value("${file.resource.content.defaultCover}")
    private String defaultCoverPath;
    @Value("${elasticsearch.server.index}")
    private String indexName;

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
            log.error("获取用户文章列表失败:", e);
            throw new CloudBlogException("获取用户文章列表失败:"+e.getMessage(), CommonError.INTERNAL_ERROR);
        }
    }

    @Override
    public AjaxResult getIndexPostList(PostPo po, String cursor, Integer size, String sortBy, String tag) {
        // 判断是否有用户id（是否登录），以此去配置推荐算法
        boolean withInterest = true;
        Object data;
        if (po.getUserId() == null) {
            data = null;
        } else {
            data = interestService.getInterestInfo(po.getUserId()).get("data");
        }
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
                return AjaxResult.success("发布成功", po.getPostId());
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
            // 同步ES 创建文章时不同步es
//            ESPost esPost = new ESPost();
//            BeanUtils.copyProperties(posts, esPost);
//            esPost.setContent(po.getContent());
//            updatePost(List.of(esPost));
            // 自动执行合规性检测
            this.checkCompliance(po);
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
            // 查询源文
            Posts sourcePost = postMapper.selectById(po.getPostId());
            // 更新草稿
            PostsContent postContent = new PostsContent();
            postContent.setId(sourcePost.getContentId());
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

    @Override
    public AjaxResult getOtherUserPostList(Long userId, Long loginUserId, String cursor, Integer size, String sortBy, String tag) {
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
            List<UserPostVo> posts = postMapper.getOtherUserPostList(userId, loginUserId, lastId, lastCreateTime, size + 1);

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
            log.error("获取用户文章列表失败:", e);
            throw new CloudBlogException("获取用户文章列表失败:,"+e.getMessage(), CommonError.INTERNAL_ERROR);
        }
    }

    @Override
    public AjaxResult syncES() throws IOException, InterruptedException {
        // 同步ES;
        // 1. 参数设置
        int pageSize = 50; // 每页大小
        Long total = postMapper.selectCount(new LambdaQueryWrapper<>(Posts.class).eq(Posts::getStatus, PostStatus.PUBLISHED.getCode()));
        long totalPages = (total + pageSize - 1) / pageSize; // 计算总页数

        // 结果
        StringBuffer processLog = new StringBuffer();

        // 初始化线程间通信组件
        // 有界队列，用于生产者和消费者之间传递数据
        BlockingQueue<List<ESPost>> dataQueue = new LinkedBlockingQueue<>((int) totalPages);
        // 计数器，用于等待所有数据查询任务完成
        CountDownLatch countDownLatch = new CountDownLatch((int) totalPages);
        Thread syncThread = new Thread(() -> {
            try {
                long handleCount = 0;
                while (handleCount < totalPages) {
                    BulkRequest bulkRequest = new BulkRequest();
                    List<ESPost> docs = dataQueue.poll(2, TimeUnit.SECONDS);
                    for (ESPost doc : docs) {
                        IndexRequest request = new IndexRequest("posts_index")
                                .id(doc.getId().toString())
                                .source(objectMapper.writeValueAsString(doc), XContentType.JSON);
                        bulkRequest.add(request);
                    }

                    BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    handleCount++;

                    if (response.hasFailures()) {
                        log.warn("批量同步部分失败：{}", response.buildFailureMessage());
                        processLog.append("批量同步部分失败：").append(response.buildFailureMessage()).append("\n");
                    } else {
                        log.info("批次批量同步成功，共：{}", docs.size());
                        processLog.append("批次批量同步成功，共：").append(docs.size()).append("\n");
                    }
                }
            } catch (Exception e) {
                log.error("数据同步出现错误", e);
                processLog.append("数据同步出现错误").append(e.getMessage()).append("\n");
            }
        });

        syncThread.start();

        for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
            final int currentPage = pageNum;
            exportTaskExecutor.execute(() -> {
                try {
                    int offset = (currentPage - 1) * pageSize;
                    log.info("开始查询第 {} 页数据, offset: {}", currentPage, offset);
                    Page<ESPost> page = new Page<>(currentPage, pageSize);
                    List<ESPost> pageData = postMapper.selectAllPostWithContent(page).getRecords();
                    // 处理正文
                    // posts.forEach(post -> post.setContent(HtmlUtil.removeHtmlTag(post.getContent())));
                    // 等级赋值
                    pageData.forEach(post -> {
                        post.setAuthorLevel(getUserLevel(post.getExp()));
                    });
                    // 将查询到的数据放入队列
                    dataQueue.put(pageData);
                    log.info("第 {} 页数据查询完成，共 {} 条", currentPage, pageData.size());
                } catch (Exception e) {
                    log.error("查询第 {} 页数据时发生异常", currentPage, e);
                } finally {
                    // 无论成功与否，都需要计数减一
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        // 等待同步线程完成
        syncThread.join();
        return AjaxResult.success("同步完成", processLog.toString());
    }

    @Override
    public AjaxResult search(String searchKey, String publishTime, String level, String sort, Integer isVipOnly, Integer size, String cursor) throws IOException {
        SearchRequest request = new SearchRequest(indexName);

        SearchSourceBuilder source = new SearchSourceBuilder();

        // ===== 查询条件 =====
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        // 关键词匹配（加权）
        MultiMatchQueryBuilder mm = QueryBuilders.multiMatchQuery(searchKey)
                .field("title", 5)
                .field("introduction", 3)
                .field("content", 1);

        bool.must(mm);
        bool.filter(QueryBuilders.termQuery("status", 2)); // 只查已发布

        // 是否会员文章
        if (isVipOnly != null && isVipOnly == 1) {
            bool.filter(QueryBuilders.termQuery("isVip", 1));
        }

        // 处理发布时间
        if (publishTime != null && !publishTime.isEmpty()) {
            Map<String,String> timeRange = ESUtil.convertTimeRange(publishTime);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createTime")
                    .gte(timeRange.get("startTime"))
                    .lte(timeRange.get("endTime"));
            bool.filter(rangeQuery);
        }

        // 处理等级
        if (level != null && !level.isEmpty()) {
            Map<String, Integer> levelRange = ESUtil.convertLevel(level);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("authorLevel")
                    .gte(levelRange.get("minLevel"));
            bool.filter(rangeQuery);
        }

        source.query(bool);

        // 处理排序
        if (sort != null && sort.equals("new")) {
            source.sort("createTime", SortOrder.DESC);
            source.sort("id", SortOrder.DESC); // 防止重复
        } else if (sort != null && sort.equals("hot")) {
            ScriptSortBuilder hotSort = SortBuilders.scriptSort(
                    new Script(
                            "doc['viewCount'].value * 0.1 + " +
                                    "doc['likeCount'].value * 0.3 + " +
                                    "doc['commentCount'].value * 0.2 + " +
                                    "doc['collectCount'].value * 0.2"
                    ),
                    ScriptSortBuilder.ScriptSortType.NUMBER
            );
            hotSort.order(SortOrder.DESC);
            source.sort(hotSort);
            source.sort("id", SortOrder.DESC);
        } else {
            FunctionScoreQueryBuilder fsq = QueryBuilders.functionScoreQuery(
                    bool,
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    ScoreFunctionBuilders.fieldValueFactorFunction("viewCount").factor(0.1f)
                            ),
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    ScoreFunctionBuilders.fieldValueFactorFunction("likeCount").factor(0.3f)
                            ),
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    ScoreFunctionBuilders.fieldValueFactorFunction("commentCount").factor(0.2f)
                            ),
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    ScoreFunctionBuilders.gaussDecayFunction("createTime", "now", "7d", "0d", 0.5)
                            )
                    }
            ).boostMode(CombineFunction.SUM);

            source.query(fsq);

            source.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
            source.sort("id", SortOrder.DESC);
        }

        // 处理分页
        size = size == null || size <= 0 ? 10 : size;
        source.size(size);

        if (cursor != null && !cursor.isEmpty()) {
            // 将字符串转为数组
            String[] cursorArr = cursor.split(",");
            source.searchAfter(cursorArr);
        }

        // 处理高亮
        HighlightBuilder hb = new HighlightBuilder();
        hb.field("title").field("introduction");
        source.highlighter(hb);

        // ===== 统计总数 =====
        source.trackTotalHits(true);

        request.source(source);

        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        SearchHit[] hits = response.getHits().getHits();
        boolean hasNext = hits.length == size;

        // 解析结果
        PageResponse<ESPost> res = new PageResponse<>();
        List<ESPost> resList = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            ESPost esPost = objectMapper.convertValue(hit.getSourceAsMap(), ESPost.class);

            // 处理时间格式 - 支持多种可能的格式
            Object createTimeObj = esPost.getCreateTime();
            if (createTimeObj != null) {
                LocalDateTime parsedTime;
                String timeStr = createTimeObj.toString();

                if (timeStr.contains("T")) {
                    // 如果包含 'T'，则认为是 ISO 格式
                    parsedTime = LocalDateTime.parse(timeStr);
                } else {
                    // 否则是自定义格式 yyyy-MM-dd HH:mm:ss
                    parsedTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }

                esPost.setCreateTime(parsedTime);
            }

            // 处理高亮
            Map<String, HighlightField> hf = hit.getHighlightFields();
            if (hf.get("title") != null) {
                esPost.setTitle(hf.get("title").fragments()[0].string());
            }
            if (hf.get("introduction") != null) {
                esPost.setIntroduction(hf.get("introduction").fragments()[0].string());
            }

            resList.add(esPost);
        }
        res.setContent(resList);
        res.setTotalElements(Long.parseLong(String.valueOf(hits.length)));
        if (hasNext) {
            res.setHasNext(true);
            Object[] nextSearchAfter = hits[hits.length - 1].getSortValues();
            res.setNextCursor(Arrays.toString(nextSearchAfter));
        } else {
            res.setHasNext(false);
            res.setNextCursor(null);
        }

        TotalHits totalHits = response.getHits().getTotalHits();
        if (totalHits != null) {
            res.setTotalElements(totalHits.value);
            log.info("查询总数约为{}", totalHits.relation.name());
        } else {
            res.setTotalElements(0L);
        }

        return AjaxResult.success(res);
    }

    @Override
    public AjaxResult delete(DeletePostPo po) {
        // 检查文章作者
        if (!postMapper.selectById(po.getPostId()).getAuthorId().equals(po.getUserId())) {
            return AjaxResult.error("您没有权限删除此文章");
        }
        int update = postMapper.update(new LambdaUpdateWrapper<>(Posts.class)
                .eq(Posts::getId, po.getPostId())
                .set(Posts::getStatus, PostStatus.DELETED.getCode())
        );
        // 同步删除 ES
        exportTaskExecutor.execute(() -> {
            try {
                DeleteResponse delete = esClient.delete(
                        new DeleteRequest(indexName, String.valueOf(po.getPostId())),
                        RequestOptions.DEFAULT
                );
                log.info("ES同步删除结果：{}", delete.status());
            } catch (IOException e) {
                log.error("ES同步删除失败：{}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
        return update > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
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
            posts.setId(draft.getId());
            // 插入文章标签表
            interestService.addPostTag(po.getTagIds(), po.getPostId());
            // 同步 ES
            // 获取文章信息
            ESPost esPost = postMapper.getESPostInfo(posts.getId());
            updatePost(List.of(esPost));
            // 检测合规性
            this.checkCompliance(po);
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
//        if (goOn && po.getCategoryId() == null) {
//            errMsg = "文章分类不能为空";
//            goOn = false;
//        }
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

    /**
     * 更新ES文章
     */
    public void updatePost(List<ESPost> posts) throws IOException {
        if (posts == null || posts.isEmpty()) {
            log.warn("ES 更新列表为空，跳过更新");
            return;
        }

        try {
            // 检查 ES 客户端连接
            if (esClient == null) {
                log.error("ES 客户端未初始化");
                throw new RuntimeException("ES 客户端未初始化");
            }

            for (ESPost post : posts) {
                if (post.getId() == null) {
                    log.warn("文章 ID 为空，跳过该条记录：{}", post);
                    continue;
                }

                IndexRequest request = new IndexRequest("posts_index")
                        .id(post.getId().toString())
                        .source(objectMapper.writeValueAsString(post), XContentType.JSON);

                // 获取响应并检查结果
                IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
                log.info("ES 更新成功 - ID: {}, Result: {}", post.getId(), response.getResult());
            }

            // 强制刷新索引，确保数据立即可见
            RefreshRequest refreshRequest = new RefreshRequest("posts_index");
            esClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
            log.info("ES 索引已刷新");

        } catch (IOException e) {
            log.error("ES 更新失败", e);
            throw e;
        } catch (Exception e) {
            log.error("ES 更新过程中发生未知异常", e);
            throw new RuntimeException("ES 更新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取用户等级
     * @param exp
     * @return
     */
    private Integer getUserLevel(Integer exp) {
        AtomicReference<Integer> level = new AtomicReference<>(1);
        TreeMap<Integer, Integer> levelMap = contentStartupConfig.getLevelMap();
        if (levelMap == null || levelMap.isEmpty()) {
            return 1;
        }
        AtomicBoolean isFound = new AtomicBoolean(false);
        levelMap.forEach((singleLevel, expThreshold) -> {
            if (isFound.get()) {
                return;
            }
            if (exp < expThreshold) {
                isFound.set(true);
                return;
            }
            level.set(singleLevel);
        });
        return level.get();
    }

    /**
     * 检测文章合规性
     */
    public void checkCompliance(PublishPostPo po) {
        exportTaskExecutor.execute(() -> {
            try {
                // 构建检测报告
                CheckReport checkReport = new CheckReport();
                checkReport.setCheckId(po.getPostId().toString() + System.currentTimeMillis());// 当前时间字符串+文章id
                checkReport.setPostId(po.getPostId());
                checkReport.setPostName(po.getTitle());
                List<CheckReport.CheckItem> checkItems = new ArrayList<>();

                log.info("开始对文章进行合规性（敏感词）检测 - 文章ID: {}", po.getPostId());
                ContentComplianceChecker.ComplianceResult result = complianceChecker.checkCompliance(HtmlUtil.removeHtmlTag(po.getContent()));
                if (!result.isCompliant()) {
                    log.warn("文章检测到违规内容 - 文章ID: {}, 违规词数量: {}, 违规词: {}",
                            po.getPostId(), result.getViolationCount(), result.getFoundWords());
                    checkItems.add(new CheckReport.CheckItem("敏感词检测", "违规词数量: "+result.getViolationCount() + "违规词: "+result.getFoundWords()));
                } else {
                    checkItems.add(new CheckReport.CheckItem("敏感词检测", "违规词数量: " + 0 + "违规词: "+ "无"));
                }
                // TODO 添加其他合规性检测逻辑

                checkReport.setCheckItems(checkItems);
                // 插入检测记录表
                postMapper.insertCheckReport(null, checkReport.getPostId(), objectMapper.writeValueAsString(checkReport));
            } catch (Exception e) {
                log.error("合规性检测失败 - 文章ID: {}, 错误: {}", po.getPostId(), e.getMessage());
            }
        });
    }
}
