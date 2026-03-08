package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.NotificationType;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.ESPost;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Dto.PostAndShareInfo;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.pojo.Po.ReviewOpinionPo;
import com.cloudblog.common.pojo.Vo.ContentReviewVo;
import com.cloudblog.common.pojo.Vo.IndexShareVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.ManagerMapper;
import com.cloudblog.content.service.ManagerService;
import com.cloudblog.content.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
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

    @Value("${elasticsearch.server.index}")
    private String indexName;

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
        } catch (Exception e) {
            log.error("处理内容失败");
            return AjaxResult.error("处理内容失败");
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
}
