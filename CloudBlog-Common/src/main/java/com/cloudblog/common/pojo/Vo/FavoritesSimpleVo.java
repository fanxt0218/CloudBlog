package com.cloudblog.common.pojo.Vo;

import com.cloudblog.common.pojo.DoMain.Favorites;
import lombok.Data;

@Data
public class FavoritesSimpleVo extends Favorites {

    private Integer collectCount;
}
