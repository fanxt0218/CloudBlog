package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.Category;
import com.cloudblog.common.pojo.Po.AddCategoryPo;
import com.cloudblog.common.pojo.Po.CategoryDetailPo;
import com.cloudblog.common.result.AjaxResult;

public interface CategoryService {

    /**
     * 获取用户分类信息
     * @param userId
     * @return
     */
    AjaxResult getCategoryInfo(Long userId);

    AjaxResult getCategoryDetail(CategoryDetailPo po);

    AjaxResult addCategory(AddCategoryPo po);

    AjaxResult editCategory(Category category);

    AjaxResult deleteCategory(Integer categoryId);
}
