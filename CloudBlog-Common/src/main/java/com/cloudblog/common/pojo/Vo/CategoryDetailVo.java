package com.cloudblog.common.pojo.Vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudblog.common.pojo.DoMain.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryDetailVo {

    private CategoryInfoListVo.CategoryInfo category;

    private IPage<UserPostVo> posts;
}
