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
import java.util.ArrayList;
import java.util.List;

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
        // 创作历程
        List<UserAchievementVo.CreativeProcess> userCreativeProcess = getUserCreativeProcess(userId);

        UserAchievementVo userAchievementVo = new UserAchievementVo();
        userAchievementVo.setLikeCount(likeCount);
        userAchievementVo.setCollectCount(collectCount);
        userAchievementVo.setCommentCount(commentCount);
        userAchievementVo.setRank(blogRank);
        userAchievementVo.setCreativeProcessList(userCreativeProcess);
        return AjaxResult.success(userAchievementVo);
    }

    /**
     * 获取用户创作历程，目前是按照年计算。计算出每年创作的文章数
     * @param userId
     * @return
     */
    public List<UserAchievementVo.CreativeProcess> getUserCreativeProcess(Long userId) {
        if (userId == null) {
            return null;
        }
        //计算某个用户每年的创作文章数，不足一年的按照当前年份
        List<UserAchievementVo.CreativeProcess> creativeProcessList = new ArrayList<>();
        creativeProcessList = userPostInfoMapper.getUserCreativeProcess(userId);
        // 如果没有创作过文章或者当前年创作文章数为0，则展示当前年份的0
        if (creativeProcessList.isEmpty() || creativeProcessList.get(0).getYear() != LocalDate.now().getYear()) {
            creativeProcessList.add(new UserAchievementVo.CreativeProcess(LocalDate.now().getYear(),0));
        }
        return creativeProcessList;
    }
}
