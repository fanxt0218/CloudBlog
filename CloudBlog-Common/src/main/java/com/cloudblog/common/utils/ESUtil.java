package com.cloudblog.common.utils;

import com.cloudblog.common.pojo.Dto.ESPost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;

public class ESUtil {

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
}
