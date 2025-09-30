package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Category;

public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 获取分类下的文章数量
     * @param id
     * @return
     */
    Integer getPostCountInCategory(Integer id);
}
