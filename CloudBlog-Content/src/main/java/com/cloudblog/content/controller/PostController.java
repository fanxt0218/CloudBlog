package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.AddBrowseCountPo;
import com.cloudblog.common.pojo.Po.PostPo;
import com.cloudblog.common.pojo.Po.PublishPostPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取首页文章列表
     */
    @PostMapping("/getIndexPostList")
    public AjaxResult getIndexPostList(
            @RequestBody PostPo po,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String tag) {
        return postService.getIndexPostList(po, cursor, size, sortBy, tag);
    }

    /**
     * 增加浏览
     */
    @PostMapping("/addBrowseCount")
    public AjaxResult addBrowseCount(@RequestBody AddBrowseCountPo po) {
        return postService.addBrowseCount(po);
    }

    /**
     * 发布文章
     */
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody PublishPostPo po) {
        return postService.publish(po);
    }
}
