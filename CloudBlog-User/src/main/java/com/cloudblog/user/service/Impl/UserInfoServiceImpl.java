package com.cloudblog.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.pojo.Dto.*;
import com.cloudblog.common.pojo.Vo.UserAchievementVo;
import com.cloudblog.common.pojo.Vo.UserHomePageVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.user.mapper.*;
import com.cloudblog.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private BrowseMapper browseMapper;
    @Autowired
    private UserPostInfoMapper userPostInfoMapper;
    @Autowired
    private UserFocusMapper userFocusMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public AjaxResult getUserInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 用户信息
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));
        UserHomePageVo userHomePageVo = new UserHomePageVo();
        userHomePageVo.setUserName(userInfo.getUserName());
        userHomePageVo.setRegion(userInfo.getRegion());
        userHomePageVo.setJoinTime(userInfo.getCreateTime());
        userHomePageVo.setIntroduction(userInfo.getIntroduction());
        userHomePageVo.setBlogAge(LocalDate.now().getYear() - userInfo.getCreateTime().getYear());
        // 用户访问量
        Long browseCount = browseMapper.selectCount(new LambdaQueryWrapper<Browse>().eq(Browse::getUserId, userId));
        // 文章数
        Long postCount = userPostInfoMapper.selectCount(new LambdaQueryWrapper<Posts>()
                .eq(Posts::getAuthorId, userId)
                .eq(Posts::getStatus, PostStatus.REVIEWING.getCode()));
        // 粉丝数
        Long fanCount = userFocusMapper.selectCount(new LambdaQueryWrapper<UserFocus>().eq(UserFocus::getFocusUserId, userId));
        userHomePageVo.setVisits(browseCount);
        userHomePageVo.setPostCount(postCount);
        userHomePageVo.setFanCount(fanCount);

        return AjaxResult.success(userHomePageVo);
    }

    @Override
    public AjaxResult getUserAchievement(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 点赞数
        Integer likeCount = likeMapper.getUserLikeCount(userId);
        // 收藏数
        Integer collectCount = collectMapper.getUserCollectCount(userId);
        // 评论数
        Integer commentCount = commentMapper.getUserCommentCount(userId);
        //TODO 博客排名 后续做定时任务周期计算
        Integer blogRank = 1;

        UserAchievementVo userAchievementVo = new UserAchievementVo();
        userAchievementVo.setLikeCount(likeCount);
        userAchievementVo.setCollectCount(collectCount);
        userAchievementVo.setCommentCount(commentCount);
        userAchievementVo.setRank(blogRank);
        return AjaxResult.success(userAchievementVo);
    }
}
