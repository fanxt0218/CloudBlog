package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.Category;
import com.cloudblog.common.pojo.Po.CategoryDetailPo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 获取分类下的文章数量
     * @param id
     * @return
     */
    Integer getPostCountInCategory(Integer id);

    /**
     * 获取分类下的文章信息
     * @param po
     */
    IPage<UserPostVo> getPostInfoByCategoryId(Page<UserPostVo> page, @Param("po") CategoryDetailPo po);
}
