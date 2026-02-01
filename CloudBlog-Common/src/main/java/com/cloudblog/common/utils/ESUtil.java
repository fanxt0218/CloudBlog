package com.cloudblog.common.utils;

import com.cloudblog.common.pojo.Dto.ESPost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ESUtil {

    // 定义时间范围常量
    private static final String ALL = "all";
    private static final String WEEK = "week";
    private static final String MONTH = "month";
    private static final String QUARTER = "quarter";
    private static final String YEAR = "year";

//    private RestHighLevelClient client;
//
//
//
//    public ESUtil(RestHighLevelClient client) {
//        this.client = client;
//    }
//
//    public static void addPost(List<ESPost> posts) {
//        IndexRequest request = new IndexRequest("posts_index")
//                .id(doc.getId().toString())   // 用数据库ID做ES文档ID
//                .source(JSON.toJSONString(doc), XContentType.JSON);
//
//        client.index(request, RequestOptions.DEFAULT);
//    }

    /**
     * 将时间范围字符串转换为具体的开始时间和结束时间
     * @param timeRange 时间范围字符串 (all, week, month, quarter, year)
     * @return 包含开始时间和结束时间的Map
     */
    public static Map<String, String> convertTimeRange(String timeRange) {
        Map<String, String> timeRangeMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime;
        LocalDateTime endDateTime = now; // 结束时间始终是当前时间

        switch (timeRange.toLowerCase()) {
            case ALL:
                // 全部时间，起始时间设为很久以前
                startDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
                break;
            case WEEK:
                // 一周前的此时此刻
                startDateTime = now.minusWeeks(1);
                break;
            case MONTH:
                // 一个月前的此时此刻
                startDateTime = now.minusMonths(1);
                break;
            case QUARTER:
                // 一个季度前（3个月前）
                startDateTime = now.minusMonths(3);
                break;
            case YEAR:
                // 一年前的此时此刻
                startDateTime = now.minusYears(1);
                break;
            default:
                // 默认情况下，返回全部时间范围
                startDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
                break;
        }

        // 格式化时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        timeRangeMap.put("startTime", startDateTime.format(formatter));
        timeRangeMap.put("endTime", endDateTime.format(formatter));

        return timeRangeMap;
    }

    /**
     * 将等级字符串转换为具体的等级数字
     */
    public static Map<String, Integer> convertLevel(String level) {
        Map<String, Integer> levelMap = new HashMap<>();
        switch (level.toLowerCase()) {
            case "all":
                levelMap.put("minLevel", 0);
                break;
            case "2":
                levelMap.put("minLevel", 2);
                break;
            case "5":
                levelMap.put("minLevel", 5);
                break;
            case "8":
                levelMap.put("minLevel", 8);
                break;
            case "9":
                levelMap.put("minLevel", 9);
        }
        return levelMap;
    }
}
