package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Po.RemoveInterestPo;
import com.cloudblog.common.pojo.Vo.TagClassVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterestMapper extends BaseMapper<Tag> {

    /**
     * 获取用户兴趣
     * @param userId
     * @return
     */
    List<Tag> getUserInterest(Long userId);

    /**
     * 批量更新用户兴趣
     * @param interests
     */
    void upgradeUserInterest(@Param("interests") List<UserInterest> interests);

    /**
     * 获取标签分类列表
     * @return
     */
    List<TagClassVo> getTagClassList();

    /**
     * 移除用户兴趣
     * @param po
     */
    void removeInterest(RemoveInterestPo po);
}
