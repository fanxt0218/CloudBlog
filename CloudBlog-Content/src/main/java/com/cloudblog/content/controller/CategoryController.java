package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.DoMain.Category;
import com.cloudblog.common.pojo.Po.AddCategoryPo;
import com.cloudblog.common.pojo.Po.CategoryDetailPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取用户分类详情
     */
    @PostMapping("/getCategoryDetail")
    public AjaxResult getCategoryDetail(@RequestBody CategoryDetailPo po) {
        return categoryService.getCategoryDetail(po);
    }

    /**
     * 新增分类
     */
    @PostMapping("/addCategory")
    public AjaxResult addCategory(@RequestBody AddCategoryPo po) {
        return categoryService.addCategory(po);
    }

    /**
     * 编辑分类
     */
    @PostMapping("/editCategory")
    public AjaxResult editCategory(@RequestBody Category category) {
        return categoryService.editCategory(category);
    }

    /**
     * 删除分类
     */
    @PostMapping("/deleteCategory")
    public AjaxResult deleteCategory(@RequestParam Integer categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

}
