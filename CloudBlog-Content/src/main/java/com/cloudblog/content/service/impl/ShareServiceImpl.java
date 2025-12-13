package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.DoMain.Share;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import com.cloudblog.common.pojo.Vo.UserShareVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.ShareMapper;
import com.cloudblog.content.service.ShareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;

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
            List<UserShareVo> shares = shareMapper.getIndexPostList(lastId, lastCreateTime, size + 1, topicId);

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
            return AjaxResult.error("获取首页动态列表失败: " + e.getMessage());
        }
    }

    @Override
    public AjaxResult getTopicList() {
        return AjaxResult.success(shareMapper.getTopicList());
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
}
