package com.cloudblog.content.controller;

import com.cloudblog.common.annotation.AutoAddExp;
import com.cloudblog.common.enums.ExpSource;
import com.cloudblog.common.pojo.Po.AddBrowseCountPo;
import com.cloudblog.common.pojo.Po.DeletePostPo;
import com.cloudblog.common.pojo.Po.PostPo;
import com.cloudblog.common.pojo.Po.PublishPostPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    @AutoAddExp(ExpSource.USER_BROWSE_POST)
    @PostMapping("/addBrowseCount")
    public AjaxResult addBrowseCount(@RequestBody AddBrowseCountPo po) {
        return postService.addBrowseCount(po);
    }

    /**
     * 发布文章
     */
    @AutoAddExp(ExpSource.USER_PUBLISH_POST)
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody PublishPostPo po) {
        return postService.publish(po);
    }

    /**
     * 浏览文章
     */
    @GetMapping("/getPost")
    public AjaxResult getPost(@RequestParam Long postId, @RequestParam(required = false) Long userId) {
        return postService.getPost(postId, userId);
    }

    /**
     * 浏览量最高文章列表
     */
    @GetMapping("/getBrowseCountList")
    public AjaxResult getBrowseTopPostList(@RequestParam Integer postType) {
        return postService.getBrowseTopPostList(postType);
    }

    /**
     * 保存草稿
     */
    @PostMapping("/saveDraft")
    public AjaxResult saveDraft(@RequestBody PublishPostPo po) {
        return postService.saveDraft(po);
    }

    /**
     * 查看草稿列表
     */
    @GetMapping("/getDraftList")
    public AjaxResult getDraftList(@RequestParam Long userId) {
        return postService.getUserDraftList(userId);
    }

    /**
     * 删除文章
     */
    @PostMapping("/delete")
    public AjaxResult delete(@RequestBody DeletePostPo po) {
        return postService.delete(po);
    }

    /**
     * 同步ES
     */
    @PostMapping("/syncES")
    public AjaxResult syncES() throws IOException, InterruptedException {
        return postService.syncES();
    }


    /**
     * 搜索文章
     */
    @GetMapping("/search")
    public AjaxResult search(
            @RequestParam String searchKey,
            @RequestParam(required = false) String publishTime,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer isVipOnly,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String cursor
            ) throws IOException {
        return postService.search(searchKey, publishTime, level, sort, isVipOnly, size, cursor);
    }
}
