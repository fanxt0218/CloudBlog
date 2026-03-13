package com.cloudblog.content.controller.admin;

import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.TagClass;
import com.cloudblog.common.pojo.DoMain.Topic;
import com.cloudblog.common.pojo.Po.ContentListManagePo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.ManagerService;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin/content")
public class ContentManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 获取文章列表
     * @param po
     * @return
     * @throws IOException
     */
    @PostMapping("/postList")
    public AjaxResult postList(@RequestBody ContentListManagePo po) throws IOException {
        return managerService.getContentList(po, ContentType.POST);
    }

    /**
     * 获取动态列表
     */
    @PostMapping("/shareList")
    public AjaxResult shareList(@RequestBody ContentListManagePo po) throws IOException {
        return managerService.getContentList(po, ContentType.SHARE);
    }

    /**
     * 编辑标签
     */
    @PostMapping("/editTag")
    public AjaxResult editTag(@RequestBody Tag tag) {
        return managerService.editTag(tag);
    }

    /**
     * 编辑标签分类
     */
    @PostMapping("/editTagCategory")
    public AjaxResult editTagCategory(@RequestBody TagClass tagclass) {
        return managerService.editTagCategory(tagclass);
    }

    /**
     * 编辑话题
     */
    @PostMapping("/editTopic")
    public AjaxResult editTopic(@RequestBody Topic topic) {
        return managerService.editTopic(topic);
    }

    /**
     * 添加标签
     */
    @PostMapping("/addTag")
    public AjaxResult addTag(@RequestBody Tag tag) {
        return managerService.addTag(tag);
    }

    /**
     * 添加标签分类
     */
    @PostMapping("/addTagCategory")
    public AjaxResult addTagCategory(@RequestBody TagClass tagclass) {
        return managerService.addTagCategory(tagclass);
    }

    /**
     * 添加话题
     */
    @PostMapping("/addTopic")
    public AjaxResult addTopic(@RequestBody Topic topic) {
        return managerService.addTopic(topic);
    }

}
