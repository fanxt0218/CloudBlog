package com.cloudblog.common.pojo.Vo;

import com.cloudblog.common.pojo.DoMain.Posts;
import lombok.Data;

@Data
public class PostWithBrowseCountVo extends Posts {

    private Long browseCount;
}
