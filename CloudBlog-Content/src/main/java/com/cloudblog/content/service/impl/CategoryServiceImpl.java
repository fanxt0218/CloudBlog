package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.pojo.DoMain.Category;
import com.cloudblog.common.pojo.Vo.CategoryInfoListVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.CategoryMapper;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private PostMapper postMapper;

    @Override
    public AjaxResult getCategoryInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }

        // 查询用户分类信息
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getUserId, userId));
        CategoryInfoListVo categoryInfoListVo = new CategoryInfoListVo();
        if (!categories.isEmpty()) {
            ArrayList<CategoryInfoListVo.CategoryInfo> categoryInfoList = new ArrayList<>();
            for (Category category : categories) {
                CategoryInfoListVo.CategoryInfo categoryInfo = new CategoryInfoListVo.CategoryInfo();
                categoryInfo.setCategoryId(category.getId());
                categoryInfo.setCategoryName(category.getCategoryName());
                categoryInfo.setImage(category.getImage());
                // 分类下文章数
                Integer postCount = categoryMapper.getPostCountInCategory(category.getId());
                categoryInfo.setPostCount(postCount);
                categoryInfoList.add(categoryInfo);
            }
            categoryInfoListVo.setCategoryInfoList(categoryInfoList);
        }
        return AjaxResult.success(categoryInfoListVo);
    }
}
