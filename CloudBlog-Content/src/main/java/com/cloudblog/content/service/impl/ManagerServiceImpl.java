package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.NotificationType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.pojo.Dto.ESPost;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Dto.PostAndShareInfo;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.pojo.Po.ReviewOpinionPo;
import com.cloudblog.common.pojo.Po.UserListPo;
import com.cloudblog.common.pojo.Po.WorkOrderListPo;
import com.cloudblog.common.pojo.Vo.*;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.PasswordUtil;
import com.cloudblog.common.utils.RedisUtil;
import com.cloudblog.content.mapper.ManagerMapper;
import com.cloudblog.content.service.ManagerService;
import com.cloudblog.content.service.NotificationService;
import com.cloudblog.content.socket.WebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.cloudblog.content.socket.WebSocket.webSocketMap;

@Slf4j
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${elasticsearch.server.index}")
    private String indexName;
    @Value("${blog.default.password}")
    private String defaultPassword;

    @Override
    public AjaxResult ContentReviewList(String title, LocalDateTime startTime, LocalDateTime endTime, Integer type, String author, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;

        if (type == null) {
            type = 0;
        }

        Page<ContentReviewVo> page = new Page<>(pageNum, pageSize);
        IPage<ContentReviewVo> list = managerMapper.getContentReviewList(page, title, startTime, endTime, type, author, pageNum, pageSize);

        return AjaxResult.success(list);
    }

    @Transactional
    @Override
    public AjaxResult ContentReview(Integer type, Long id, ReviewOpinionPo po) {
        if (null == po.getOpinion()) {
            return AjaxResult.warn("请填写审核意见");
        }
        // 查询目标状态
        PostAndShareInfo contentInfo = managerMapper.getContentInfo(type, id);
        if (!Objects.equals(contentInfo.getStatus(), PostStatus.REVIEWING.getCode())) {
            return AjaxResult.warn("内容不存在或已被处理");
        }

        // 处理内容
        try {
            managerMapper.contentReview(type, id, po.getOpinion());
            if (type.equals(ContentType.POST.ordinal())) {
                // 调整ES
//                updatePostField(id,"status",po.getOpinion());
                ESPost post = managerMapper.getESPostInfo(id);
                updatePost(List.of(post));
            }
        } catch (Exception e) {
            log.error("处理内容失败");
            CloudBlogException.cast(Arrays.toString(e.getStackTrace()), CommonError.INTERNAL_ERROR);
        }

        if (!Objects.equals(po.getOpinion(), PostStatus.PUBLISHED.getCode())) {
            // 审核未通过
            UserInfo userInfo = managerMapper.getContentAuthorInfo(type, id);
            if (userInfo != null) {
                // 发送消息
                Notification notification = new Notification();
                notification.setRecipientId(userInfo.getUserId());
                notification.setSenderId(1L);  // 管理账号id
                notification.setType(NotificationType.CHAT.getValue());
//                notification.setObjectType(type);
                notification.setObjectType(ContentType.TEXT.ordinal());
                notification.setObjectId(id);
                StringBuilder content = new StringBuilder("您的");
                if (type == ContentType.POST.ordinal()) {
                    content.append("文章");
                } else {
                    content.append("动态");
                }
                content.append("《").append(contentInfo.getIntro()).append("》");
                content.append("审核未通过");
                if (po.getReason() != null && !po.getReason().isEmpty()) {
                    content.append("，原因：").append(po.getReason()).append("。");
                }
                if (type == ContentType.POST.ordinal()) {
                    content.append("\n文章已存入您的草稿箱，修改后可重新发布");
                }
                notification.setContent(content.toString());
                notification.setCreateTime(LocalDateTime.now());

                notificationService.addNotification(notification);
            }
        }
        return AjaxResult.success("处理成功", id);
    }

    @Override
    public AjaxResult getContentList(ContentListManagePo po, ContentType type) throws IOException {
        PageResponse pageRes = new PageResponse<>();
        if (type == ContentType.POST) {
            pageRes = searchPostList(po);
        } else {
            pageRes = searchShareList(po);
        }
        return AjaxResult.success(pageRes);
    }

    @Override
    public AjaxResult editTag(Tag tag) {
        if (tag.getId() == null || tag.getClassId() == null) {
            return AjaxResult.warn("参数错误");
        }
        // 查询分类是否存在
        TagClass tagInfo = managerMapper.getTagClassInfo(tag.getClassId());
        if (tagInfo == null) {
            return AjaxResult.warn("分类不存在");
        }
        managerMapper.editTag(tag);
        return AjaxResult.success("修改成功");
    }

    @Override
    public AjaxResult editTagCategory(TagClass tagClass) {
        if (tagClass.getId() == null) {
            return AjaxResult.warn("参数错误");
        }
        // 删除标签分类
        if (null != tagClass.getStatus() && tagClass.getStatus() == 1) {
            // 查询该分类下是否还有标签
            List<Tag> tagList = managerMapper.getTagByTagClass(tagClass.getId());
            if (tagList != null && !tagList.isEmpty()) {
                return AjaxResult.warn("该分类下有标签，请先删除标签");
            }
        }
        managerMapper.editTagClass(tagClass);
        return AjaxResult.success("修改成功");
    }

    @Override
    public AjaxResult editTopic(Topic topic) {
        if (topic.getId() == null) {
            return AjaxResult.warn("参数错误");
        }
        managerMapper.editTopic(topic);
        return AjaxResult.success("修改成功");
    }

    @Override
    public AjaxResult getUserList(UserListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() < 1 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() < 1 ? 10 : po.getPageSize();
        Page<UserDetailVo> page = new Page<>(pageNum, pageSize);
        IPage<UserDetailVo> list = managerMapper.getUserList(page, po);
        return AjaxResult.success(list);
    }

    @Override
    public AjaxResult resetPassword(Long targetId) {
        String defaultHashPass = PasswordUtil.hashPassword(defaultPassword);
        managerMapper.resetPassword(targetId, defaultHashPass);
        return AjaxResult.success("重置成功");
    }

    @Override
    public AjaxResult updateUserStatus(Long targetId, Integer status) {
        managerMapper.updateUserStatus(targetId, status);
        return AjaxResult.success("更新成功");
    }

    @Override
    public AjaxResult addTag(Tag tag) {
        if (tag.getTagName() == null || tag.getTagName().isEmpty()) {
            return AjaxResult.warn("参数错误");
        }
        if (tag.getClassId() == null){
            return AjaxResult.warn("参数错误");
        }
        // 检查标签名称
        List<Tag> tagList = managerMapper.getTagByTagName(tag.getTagName());
        if (tagList != null && !tagList.isEmpty()) {
            return AjaxResult.warn("标签名称已存在");
        }
        // 检查标签分类
        TagClass tagClass = managerMapper.getTagClassInfo(tag.getClassId());
        if (tagClass == null) {
            return AjaxResult.warn("标签分类不存在");
        }
        managerMapper.addTag(tag);
        return AjaxResult.success("添加成功");
    }

    @Override
    public AjaxResult addTagCategory(TagClass tagclass) {
        if (tagclass.getClassName() == null || tagclass.getClassName().isEmpty()) {
            return AjaxResult.warn("参数错误");
        }
        List<TagClass> tagClassList = managerMapper.getTagClassByClassName(tagclass.getClassName());
        if (tagClassList != null && !tagClassList.isEmpty()) {
            return AjaxResult.warn("标签分类已存在");
        }
        managerMapper.addTagClass(tagclass);
        return AjaxResult.success("添加成功");
    }

    @Override
    public AjaxResult addTopic(Topic topic) {
        if (topic.getTopicName() == null || topic.getTopicName().isEmpty() || topic.getImage().isEmpty()) {
            return AjaxResult.warn("参数错误");
        }
        List<Topic> topicList = managerMapper.getTopicByTopicName(topic.getTopicName());
        if (topicList != null && !topicList.isEmpty()) {
            return AjaxResult.warn("标签分类已存在");
        }
        managerMapper.addTopic(topic);
        return AjaxResult.success("添加成功");
    }

    @Override
    public AjaxResult editUser(UserInfo userInfo) {
        if (userInfo.getUserId() == null) {
            return AjaxResult.warn("参数错误");
        }
        if (userInfo.getUserName() == null || userInfo.getUserName().isEmpty()) {
            return AjaxResult.warn("用户名不能为空");
        }
        managerMapper.editUser(userInfo);
        return AjaxResult.success("修改成功");
    }

    @Override
    public AjaxResult getWorkOrderList(WorkOrderListPo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() < 1 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() < 1 ? 10 : po.getPageSize();
        Page<WorkOrderVo> page = new Page<>(pageNum, pageSize);
        IPage<WorkOrderVo> list = managerMapper.getWorkOrderList(page, po);
        return AjaxResult.success(list);
    }

    @Override
    public AjaxResult handleWorkOrder(WorkOrder workOrder) {
        if (workOrder.getId() == null || workOrder.getStatus() == null) {
            return AjaxResult.warn("参数错误");
        }
        managerMapper.handleWorkOrder(workOrder);
        return AjaxResult.success("处理成功");
    }

    @Override
    public AjaxResult getRedisValue(String key) {
        return AjaxResult.success(redisUtil.get(key));
    }

    @Override
    public AjaxResult refreshCache(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            redisUtil.clearAll();
            return AjaxResult.success();
        }
        for (String key : keys) {
            redisUtil.clear(key);
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult getIndexDefine() {
        try {
            // 从项目根目录的 lib 文件夹读取索引配置文件
            String projectRoot = System.getProperty("user.dir");
            String indexPath = projectRoot + java.io.File.separator + "lib" + java.io.File.separator + "posts_index.json";

            java.io.File file = new java.io.File(indexPath);
            if (!file.exists()) {
                log.error("索引配置文件不存在：{}", indexPath);
                return null;
            }

            // 读取文件内容
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            String indexDefine = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

            log.info("成功读取 ES 索引配置文件，路径：{}", indexPath);
            return AjaxResult.success(indexDefine);
        } catch (Exception e) {
            log.error("读取 ES 索引配置文件失败", e);
            return AjaxResult.error("读取 ES 索引配置文件失败");
        }
    }

    @Override
    public AjaxResult getTotalArticleCount() {
        Long totalPostsCount = managerMapper.getTotalPostsCount();
        Long totalShareCount = managerMapper.getTotalSharesCount();

        return AjaxResult.success(totalPostsCount + totalShareCount);
    }

    @Override
    public AjaxResult getDataBoardUser() {
        // 获取用户总数
        Long totalUserCount = managerMapper.getTotalUserCount();
        // 获取在线用户数
        int onlineUserCount = webSocketMap.size();
        DataBoardUserVo dataBoardUserVo = new DataBoardUserVo();
        dataBoardUserVo.setTotalUser(totalUserCount);
        dataBoardUserVo.setOnlineUser(onlineUserCount);
        return AjaxResult.success(dataBoardUserVo);
    }

    @Override
    public AjaxResult getHotArticle(Integer limit) {
        // 设置默认值，如果 limit 为空或超出范围
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        // 限制最大返回数量，避免性能问题
        if (limit > 100) {
            limit = 100;
        }

        List<HotArticleVo> hotArticleList = managerMapper.getHotArticleTop10(limit);

        // 处理空数据情况
        if (hotArticleList == null || hotArticleList.isEmpty()) {
            log.info("暂无热门文章数据");
            return AjaxResult.success(new ArrayList<>());
        }

        log.info("查询到 {} 篇热门文章", hotArticleList.size());
        return AjaxResult.success(hotArticleList);
    }

    /**
     * 搜索文章列表
     */
    private PageResponse<ESPost> searchPostList(ContentListManagePo po) throws IOException {
        int pageNum = po.getPageNum() == null || po.getPageNum() < 1 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() < 1 ? 10 : po.getPageSize();

        SearchRequest request = new SearchRequest(indexName);

        SearchSourceBuilder source = new SearchSourceBuilder();

        // ===== 查询条件 =====
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        // 关键词匹配（加权）
        if (po.getTitle() != null && !po.getTitle().isEmpty()) {
            MultiMatchQueryBuilder mm = QueryBuilders.multiMatchQuery(po.getTitle())
                    .field("title", 5)
                    .field("introduction", 3)
                    .field("content", 1);

            bool.must(mm);
        }
        bool.filter(QueryBuilders.termQuery("status", 2)); // 只查已发布

        // 作者
        if (po.getAuthorName() != null && !po.getAuthorName().isEmpty()) {
            bool.filter(QueryBuilders.matchQuery("authorName", po.getAuthorName()));
        }

        // 是否会员文章
        if (po.getVip() != null) {
            bool.filter(QueryBuilders.termQuery("isVip", po.getVip()? 1:0));
        }

        // 处理发布时间
        // 格式化时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (po.getStartPublishTime() != null) {
            RangeQueryBuilder startTimeRange = QueryBuilders.rangeQuery("createTime")
                    .gte(po.getStartPublishTime().format(formatter));
            bool.filter(startTimeRange);
        }
        if (po.getEndPublishTime() != null) {
            RangeQueryBuilder endTimeRange = QueryBuilders.rangeQuery("createTime")
                    .lte(po.getEndPublishTime().format(formatter));
            bool.filter(endTimeRange);
        }

        source.query(bool);

        // 处理排序
        if (po.getSort() != null && po.getSort().equals("new")) {
            source.sort("createTime", SortOrder.DESC);
            source.sort("id", SortOrder.DESC); // 防止重复
        } else if (po.getSort() != null && po.getSort().equals("hot")) {
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

        // 处理分页 - 普通页码分页
        source.from((pageNum - 1) * pageSize);
        source.size(pageSize);

        // 处理高亮
        HighlightBuilder hb = new HighlightBuilder();
        hb.field("title").field("introduction");
        source.highlighter(hb);

        // ===== 统计总数 =====
        source.trackTotalHits(true);

        request.source(source);

        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        SearchHit[] hits = response.getHits().getHits();

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
//            Map<String, HighlightField> hf = hit.getHighlightFields();
//            if (hf.get("title") != null) {
//                esPost.setTitle(hf.get("title").fragments()[0].string());
//            }
//            if (hf.get("introduction") != null) {
//                esPost.setIntroduction(hf.get("introduction").fragments()[0].string());
//            }

            resList.add(esPost);
        }
        res.setContent(resList);
        res.setTotalElements(Long.parseLong(String.valueOf(hits.length)));

        TotalHits totalHits = response.getHits().getTotalHits();
        if (totalHits != null) {
            res.setTotalElements(totalHits.value);
            log.info("查询总数约为{}", totalHits.relation.name());
        } else {
            res.setTotalElements(0L);
        }

        return res;
    }

    /**
     * 搜索动态列表
     */
    private PageResponse<IndexShareVo> searchShareList(ContentListManagePo po) {
        int pageNum = po.getPageNum() == null || po.getPageNum() < 1 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() < 1 ? 10 : po.getPageSize();
        if (po.getTitle() == null || po.getTitle().isEmpty()) {
            po.setTitle(null);
        }
        if (po.getAuthorName() == null || po.getAuthorName().isEmpty()) {
            po.setAuthorName(null);
        }
        if (po.getSort() == null || po.getSort().isEmpty()) {
            po.setSort(null);
        }
        Page<IndexShareVo> page = Page.of(pageNum, pageSize);
        IPage<IndexShareVo> res = managerMapper.searchShareList(page, po);

        List<IndexShareVo> records = res.getRecords();
        PageResponse<IndexShareVo> pageRes = new PageResponse<>();
        pageRes.setContent(records);
        pageRes.setTotalElements(res.getTotal());
        pageRes.setCurrentPage(pageNum);
        pageRes.setPageSize(pageSize);
        return pageRes;
    }

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
     * 局部更新 ES 文章的某个字段
     * @param postId 文章 ID
     * @param fieldName 要更新的字段名
     * @param value 新的字段值
     */
    public void updatePostField(Long postId, String fieldName, Object value) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("posts_index", postId.toString())
                .doc(fieldName, value);  // 只更新这个字段

        UpdateResponse response = esClient.update(updateRequest, RequestOptions.DEFAULT);
        log.info("ES 字段更新成功 - ID: {}, 字段：{}, Result: {}", postId, fieldName, response.getResult());
    }
}
