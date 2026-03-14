package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.Category;
import com.cloudblog.common.pojo.Po.AddCategoryPo;
import com.cloudblog.common.pojo.Po.CategoryDetailPo;
import com.cloudblog.common.pojo.Vo.CategoryDetailVo;
import com.cloudblog.common.pojo.Vo.CategoryInfoListVo;
import com.cloudblog.common.pojo.Vo.UserCollectListVo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.CategoryMapper;
import com.cloudblog.content.mapper.PostMapper;
import com.cloudblog.content.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public AjaxResult getCategoryInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }

        // 查询用户分类信息
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId)
                .eq(Category::getStatus, 0)
        );
        CategoryInfoListVo categoryInfoListVo = new CategoryInfoListVo();
        if (!categories.isEmpty()) {
            ArrayList<CategoryInfoListVo.CategoryInfo> categoryInfoList = new ArrayList<>();
            for (Category category : categories) {
                CategoryInfoListVo.CategoryInfo categoryInfo = new CategoryInfoListVo.CategoryInfo();
                categoryInfo.setCategoryId(category.getId());
                categoryInfo.setCategoryName(category.getCategoryName());
                categoryInfo.setImage(category.getImage());
                categoryInfo.setDescription(category.getDescription());
                // 分类下文章数
                Integer postCount = categoryMapper.getPostCountInCategory(category.getId());
                categoryInfo.setPostCount(postCount);
                categoryInfoList.add(categoryInfo);
            }
            categoryInfoListVo.setCategoryInfoList(categoryInfoList);
        }
        return AjaxResult.success(categoryInfoListVo);
    }

    @Override
    public AjaxResult getCategoryDetail(CategoryDetailPo po) {
        if (po.getUserId() == null || po.getCategoryId() == null) {
            return AjaxResult.error("参数错误");
        }

        if (po.getPostName().isBlank()) {
            po.setPostName(null);
        }

        int pageNum = po.getPageNum() == null || po.getPageNum() <= 0 ? 1 : po.getPageNum();
        int pageSize = po.getPageSize() == null || po.getPageSize() <= 0 ? 10 : po.getPageSize();

        // 查询分类信息
        CategoryInfoListVo categories = (CategoryInfoListVo)this.getCategoryInfo(po.getUserId()).get("data");
        Optional<CategoryInfoListVo.CategoryInfo> category = categories.getCategoryInfoList()
                .stream()
                .filter(categoryInfo -> categoryInfo.getCategoryId().equals(po.getCategoryId()))
                .findFirst();
        if (category.isEmpty()) {
            return AjaxResult.error("分类不存在");
        }

        // 查询分类下的文章信息
        Page<UserPostVo> page = new Page<>(pageNum, pageSize);
        IPage<UserPostVo> posts = categoryMapper.getPostInfoByCategoryId(page,po);

        CategoryDetailVo categoryDetailVo = new CategoryDetailVo();
        categoryDetailVo.setCategory(category.get());
        categoryDetailVo.setPosts(posts);
        return AjaxResult.success(categoryDetailVo);
    }

    @Override
    public AjaxResult addCategory(AddCategoryPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (po.getCategoryName() == null || po.getCategoryName().isBlank()) {
            return AjaxResult.error("分类名称不能为空");
        }

        Category category = new Category();
        category.setUserId(po.getUserId());
        category.setCategoryName(po.getCategoryName());
        category.setImage(po.getImage());
        category.setDescription(po.getCategoryDesc());

        return categoryMapper.insert(category) > 0 ? AjaxResult.success() : AjaxResult.error("添加失败");
    }

    @Override
    public AjaxResult editCategory(Category category) {
        if (category.getId() == null) {
            return AjaxResult.error("分类ID不能为空");
        }

        return categoryMapper.updateById(category) > 0 ? AjaxResult.success() : AjaxResult.error("修改失败");
    }

    @Override
    public AjaxResult deleteCategory(Integer categoryId) {
        // 查询分类下的文章数
        Integer postCount = categoryMapper.getPostCountInCategory(categoryId);
        if (postCount > 0) {
            return AjaxResult.error("请先移除该分类下的文章");
        }
        LambdaUpdateWrapper<Category> set = new LambdaUpdateWrapper<Category>().eq(Category::getId, categoryId).set(Category::getStatus, 1);
        return categoryMapper.update(set) > 0 ? AjaxResult.success() : AjaxResult.error("删除失败");
    }
}
