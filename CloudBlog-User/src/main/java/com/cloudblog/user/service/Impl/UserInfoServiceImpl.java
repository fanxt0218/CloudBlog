package com.cloudblog.user.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.pojo.Vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.enums.PostStatus;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.PasswordUtil;
import com.cloudblog.common.utils.UploadUtil;
import com.cloudblog.content.config.ContentStartupConfig;
import com.cloudblog.content.mapper.BrowseMapper;
import com.cloudblog.content.service.*;
import com.cloudblog.user.config.UserStartupConfig;
import com.cloudblog.user.mapper.*;
import com.cloudblog.user.service.UserInfoService;
import com.cloudblog.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.cloudblog.common.result.AjaxResult.DATA_TAG;
import static com.cloudblog.user.service.Impl.UserServiceImpl.*;

@Slf4j
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
    private LikeService likeService;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private InterestService interestService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private FavoritesService favoritesService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ShareService shareService;

    @Value("${file.resource.content.avatar}")
    private String avatarPath;

    @Override
    public AjaxResult getUserInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 用户信息
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));
        UserHomePageVo userHomePageVo = new UserHomePageVo();
        userHomePageVo.setUserName(userInfo.getUserName());
        userHomePageVo.setImage(userInfo.getImage());
        userHomePageVo.setRegion(userInfo.getRegion());
        userHomePageVo.setJoinTime(userInfo.getCreateTime());
        userHomePageVo.setIntroduction(userInfo.getIntroduction());
        userHomePageVo.setBlogAge(this.getUserBlogAge(userInfo.getCreateTime()));
        // 用户访问量
        Long browseCount = browseMapper.selectCount(new LambdaQueryWrapper<Browse>().eq(Browse::getUserId, userId));
        // 文章数
        Long postCount = userPostInfoMapper.selectCount(new LambdaQueryWrapper<Posts>()
                .eq(Posts::getAuthorId, userId)
                .eq(Posts::getStatus, PostStatus.PUBLISHED.getCode()));
        // 粉丝数
        Long fanCount = userFocusMapper.selectCount(new LambdaQueryWrapper<UserFocus>().eq(UserFocus::getFocusUserId, userId));
        // 获取等级
        Integer level = getUserLevel(userInfo.getExp());

        userHomePageVo.setVisits(browseCount);
        userHomePageVo.setPostCount(postCount);
        userHomePageVo.setFanCount(fanCount);
        userHomePageVo.setLevel(level);

        // 额外部分（用户信息弹框）
        // 关注数
        Long focusCount = userFocusMapper.selectCount(new LambdaQueryWrapper<UserFocus>().eq(UserFocus::getUserId, userId));
        userHomePageVo.setFocusCount(focusCount);
        // 是否是VIP
        userHomePageVo.setIsVip(userInfo.getIsVip());

        return AjaxResult.success(userHomePageVo);
    }

    @Override
    public AjaxResult getUserAchievement(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 点赞数
        Integer likeCount = likeService.getUserLikeCount(userId);
        // 收藏数
        Integer collectCount = collectMapper.getUserCollectCount(userId);
        // 评论数
        Integer commentCount = commentService.getUserCommentCount(userId);
        // 博客排名
        Long blogRank = UserStartupConfig.USER_RANKING_MAP.get(userId);
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

    @Override
    public AjaxResult getPersonalInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 用户信息
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));
        User user = userService.getUser(userId);
        // 获取码龄
        Integer userBlogAge = this.getUserBlogAge(userInfo.getCreateTime());
        // 获取兴趣
        List<Tag> tags = (List<Tag>) interestService.getInterestInfo(userId).get(DATA_TAG);
        List<PersonalInfoVo.UserTagList> userTagList = tags.stream().map(tag -> new PersonalInfoVo.UserTagList(tag.getId(), tag.getTagName())).toList();

        PersonalInfoVo personalInfoVo = new PersonalInfoVo();
        BeanUtils.copyProperties(userInfo, personalInfoVo);
        personalInfoVo.setUserId(user.getId());
        personalInfoVo.setUserAccount(user.getUserAccount());
        personalInfoVo.setBlogAge(userBlogAge);
        personalInfoVo.setTags(userTagList);

        return AjaxResult.success(personalInfoVo);
    }

    @Override
    public AjaxResult getAccountSettings(Long userId) {
        User user = userService.getUser(userId);
        return AjaxResult.success(user);
    }

    @Override
    public AjaxResult getLikeList(UserLikeListPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        IPage<UserLikeListVo> userLikeListVoList = postService.getUserLikeList(po);
        return AjaxResult.success(userLikeListVoList);
    }

    @Override
    public AjaxResult getCollectList(UserCollectListPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        IPage<UserCollectListVo> userCollectListVoList = postService.getUserCollectList(po);
        return AjaxResult.success(userCollectListVoList);
    }

    @Override
    public AjaxResult getBrowseHistory(UserBrowseListPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        IPage<UserBrowseListVo> userBrowseListVoList = postService.getUserBrowseHistory(po);
        return AjaxResult.success(userBrowseListVoList);
    }

    @Override
    public AjaxResult getUserFavorites(Long userId) {
        return favoritesService.getUserFavorites(userId);
    }

    @Override
    public AjaxResult getInterestInfo(Long userId) {
        return interestService.getInterestInfo(userId);
    }

    @Override
    public AjaxResult getCategoryInfo(Long userId) {
        return categoryService.getCategoryInfo(userId);
    }

    @Override
    public AjaxResult getUserPostList(Long userId, Long loginUserId, String cursor, Integer size, String sortBy, String tag) {

        return loginUserId == null ?
                postService.getUserPostList(userId, cursor, size, sortBy, tag)
                :
                postService.getOtherUserPostList(userId, loginUserId, cursor, size, sortBy, tag);
    }

    @Override
    public AjaxResult getUserShareList(Long userId, String cursor, Integer size, String sortBy, String tag) {
        return shareService.getUserShareList(userId, cursor, size, sortBy, tag);
    }

    @Override
    public AjaxResult getUserLevelInfo(Long userId) {
        return levelService.getUserLevelInfo(userId);
    }

    @Override
    public AjaxResult updatePersonalInfo(UpdatePersonalInfoPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 检查账户是否重复
//        if (po.getUserAccount() != null && !po.getUserAccount().isEmpty()) {
//            User userByAccount = userService.getUserByAccount(po.getUserAccount());
//            if (userByAccount != null) {
//                return AjaxResult.warn("该用户账户已存在");
//            }
//        }
        // 检查用户昵称是否符合规范
        if (po.getUserName() != null && !po.getUserName().isEmpty()) {
            if (!checkUserName(po.getUserName())){
                return AjaxResult.warn("用户名只能由3-16位的字母、数字、下划线、汉字组成");
            }
        }
        // 执行更新
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(po, userInfo);
        userInfo.setUpdateTime(LocalDateTime.now());
        try {
            userInfoMapper.update(userInfo, new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, po.getUserId()));
        } catch (Exception e) {
            log.error("更新用户信息失败：{}", e.getMessage());
            return AjaxResult.error("更新失败");
        }
        return AjaxResult.success("更新成功");
    }

    @Override
    public AjaxResult updatePassword(UpdatePasswordPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 校验密码格式
        if (!checkPassword(po.getNewPassword())) {
            return AjaxResult.warn("密码只能由6-16位的字母、数字组成");
        }
        // 验证新旧密码
        User user = userService.getUser(po.getUserId());
        if (PasswordUtil.checkPassword(po.getNewPassword(), user.getPassword())) {
            return AjaxResult.warn("新密码不能与旧密码相同");
        }
        // 执行更新
        User updateUser = new User();
        updateUser.setId(po.getUserId());
        updateUser.setPassword(PasswordUtil.hashPassword(po.getNewPassword()));
        try {
            userService.updatePassword(updateUser);
        } catch (Exception e) {
            log.error("更新用户密码失败：{}", e.getMessage());
            return AjaxResult.error("更新失败");
        }
        return AjaxResult.success("更新成功");
    }

    @Override
    public AjaxResult updatePhone(UpdatePhonePo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 检查手机号格式
        if (!checkPhone(po.getNewPhone())) {
            return AjaxResult.warn("手机号格式不正确");
        }
        // 检查手机号是否重复
        User user = userService.getUserByPhone(po.getNewPhone());
        if (user != null) {
            return AjaxResult.warn("手机号已注册");
        }
        // 执行更新
        User updateUser = new User();
        updateUser.setId(po.getUserId());
        updateUser.setPhone(po.getNewPhone());
        try {
            userService.updatePhone(updateUser);
        } catch (Exception e) {
            log.error("更新用户手机号失败：{}", e.getMessage());
            return AjaxResult.error("更新失败");
        }
        return AjaxResult.success("更新成功");
    }

    @Override
    public AjaxResult updateEmail(UpdateEmailPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 验证邮箱格式
        if (!checkEmail(po.getNewEmail())) {
            return AjaxResult.warn("邮箱格式不正确");
        }
        // 验证邮箱是否重复
        User user = userService.getUserByEmail(po.getNewEmail());
        if (user != null) {
            return AjaxResult.warn("邮箱已被绑定");
        }
        // 执行更新
        User updateUser = new User();
        updateUser.setId(po.getUserId());
        updateUser.setEmail(po.getNewEmail());
        try {
            userService.updateEmail(updateUser);
        } catch (Exception e) {
            log.error("更新用户邮箱失败：{}", e.getMessage());
            return AjaxResult.error("更新失败");
        }
        return AjaxResult.success("更新成功");
    }

    @Override
    public AjaxResult uploadAvatar(MultipartFile file) {
        String uploadPath = avatarPath;
        String path = UploadUtil.uploadFile(file, uploadPath);
        return AjaxResult.success("上传成功", path);
    }

    @Override
    public AjaxResult removeInterest(RemoveInterestPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (po.getTagId() == null) {
            return AjaxResult.error("标签ID不能为空");
        }
        try {
            interestService.removeInterest(po);
        } catch (Exception e) {
            CloudBlogException.cast(Arrays.toString(e.getStackTrace()), CommonError.INTERNAL_ERROR);
        }
        return AjaxResult.success("删除成功");
    }

    @Override
    public AjaxResult addInterest(AddInterestPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (po.getTagId() == null) {
            return AjaxResult.error("标签ID不能为空");
        }
        try {
            interestService.addInterest(po);
        } catch (Exception e) {
            log.error("添加用户兴趣失败：{}", e.getMessage());
            CloudBlogException.cast(Arrays.toString(e.getStackTrace()), CommonError.INTERNAL_ERROR);
        }
        return AjaxResult.success("添加成功");
    }

    @Override
    public AjaxResult getDefaultFavoritesId(Long userId) {
        return AjaxResult.success(favoritesService.getUserDefaultFavorites(userId));
    }

    @Override
    public AjaxResult getIndexUserList(String cursor, Integer size, Integer type) {
        if (type == null || (type != 1 && type != 2 && type != 3)) {
            type = 0;
        }
        PageResponse<IndexUserListVo> response = new PageResponse<>();
        try{
            switch (type) {
                case 0:
                    response = this.getIndexUserListByCommon(cursor, size);
                    break;
                case 1:
                    response = this.getIndexUserListByBlogCount(cursor, size);
                    break;
                case 2:
                    response = this.getIndexUserListByExp(cursor, size);
                    break;
                case 3:
                    response = this.getIndexUserListByFanCount(cursor, size);
                    break;
                default:
                    break;
            }
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("获取用户列表失败：{}", e.getMessage());
            throw new CloudBlogException("获取用户列表失败", CommonError.INTERNAL_ERROR);
        }
    }

    @Override
    public Long getUsersCount() {
        return userService.getUsersCount();
    }

    /**
     * 获取首页用户列表(综合排名)
     */
    public PageResponse<IndexUserListVo> getIndexUserListByCommon(String cursor, Integer size) {
            // 默认参数处理
            size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

            // 解析游标
            Map<String, Object> cursorMap = parseCursor(cursor);
            Double lastScore = null;
            Long lastUserId = null;

            if (cursorMap != null) {
                Object scoreObj = cursorMap.get("score");
                Object userIdObj = cursorMap.get("userId");

                if (scoreObj != null && userIdObj != null) {
                    lastScore = Double.parseDouble(scoreObj.toString());
                    lastUserId = Long.parseLong(userIdObj.toString());
                }
            }

            // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
            List<IndexUserListVo> users = userInfoMapper.getScoreBasedUserList(lastScore, lastUserId, size + 1);

            users.forEach(user -> {
                user.setLevel(getUserLevel(user.getExp()));
            });

            // 构建PageResponse返回结果
            PageResponse<IndexUserListVo> response = new PageResponse<>();
            response.setPageSize(size);

            // 判断是否还有更多数据
            boolean hasNext = users.size() > size;
            response.setHasNext(hasNext);

            // 设置实际返回的数据列表
            if (hasNext) {
                // 移除多查的一个元素
                response.setContent(users.subList(0, size));
                // 生成下一个游标
                IndexUserListVo lastUser = users.get(size - 1);
                String nextCursor = generateCursor(lastUser);
                response.setNextCursor(nextCursor);
            } else {
                response.setContent(users);
            }
            return response;
    }

    /**
     * 获取首页用户列表(按文章数排名)
     */
    public PageResponse<IndexUserListVo> getIndexUserListByBlogCount(String cursor, Integer size) {
        // 默认参数处理
        size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

        // 解析游标
        Map<String, Object> cursorMap = parseCursor(cursor);
        Long lastPostCount = null;
        Long lastUserId = null;

        if (cursorMap != null) {
            Object postCountObj = cursorMap.get("postCount");
            Object userIdObj = cursorMap.get("userId");

            if (postCountObj != null && userIdObj != null) {
                lastPostCount = Long.parseLong(postCountObj.toString());
                lastUserId = Long.parseLong(userIdObj.toString());
            }
        }

        // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
        List<IndexUserListVo> users = userInfoMapper.getPostCountBasedUserList(lastPostCount, lastUserId, size + 1);

        // 设置等级
        setLevel(users);

        // 构建PageResponse返回结果
        PageResponse<IndexUserListVo> response = new PageResponse<>();
        response.setPageSize(size);

        // 判断是否还有更多数据
        boolean hasNext = users.size() > size;
        response.setHasNext(hasNext);

        // 设置实际返回的数据列表
        if (hasNext) {
            // 移除多查的一个元素
            response.setContent(users.subList(0, size));
            // 生成下一个游标
            IndexUserListVo lastUser = users.get(size - 1);
            String nextCursor = generatePostCountCursor(lastUser);
            response.setNextCursor(nextCursor);
        } else {
            response.setContent(users);
        }
        return response;
    }

    /**
     * 获取首页用户列表(按经验值排名)
     */
    public PageResponse<IndexUserListVo> getIndexUserListByExp(String cursor, Integer size) {
        // 默认参数处理
        size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

        // 解析游标
        Map<String, Object> cursorMap = parseCursor(cursor);
        Integer lastExp = null;
        Long lastUserId = null;

        if (cursorMap != null) {
            Object expObj = cursorMap.get("exp");
            Object userIdObj = cursorMap.get("userId");

            if (expObj != null && userIdObj != null) {
                lastExp = Integer.parseInt(expObj.toString());
                lastUserId = Long.parseLong(userIdObj.toString());
            }
        }

        // 查询数据
        List<IndexUserListVo> users = userInfoMapper.getExpBasedUserList(lastExp, lastUserId, size+1);
        // 设置等级
        setLevel(users);
        // 构建PageResponse返回结果
        PageResponse<IndexUserListVo> response = new PageResponse<>();
        response.setPageSize(size);

        // 判断是否还有更多数据
        boolean hasNext = users.size() > size;
        response.setHasNext(hasNext);

        // 设置实际返回的数据列表
        if (hasNext) {
            // 移除多查的一个元素
            response.setContent(users.subList(0, size));
            // 生成下一个游标
            IndexUserListVo lastUser = users.get(size - 1);
            String nextCursor = generateExpCursor(lastUser);
            response.setNextCursor(nextCursor);
        } else {
            response.setContent(users);
        }
        return response;
    }

    /**
     * 获取首页用户列表(按粉丝数排名)
     */
    public PageResponse<IndexUserListVo> getIndexUserListByFanCount(String cursor, Integer size) {
        // 默认参数处理
        size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

        // 解析游标
        Map<String, Object> cursorMap = parseCursor(cursor);
        Integer lastFansCount = null;
        Long lastUserId = null;

        if (cursorMap != null) {
            Object fansCountObj = cursorMap.get("fansCount");
            Object userIdObj = cursorMap.get("userId");

            if (fansCountObj != null && userIdObj != null) {
                lastFansCount = Integer.parseInt(fansCountObj.toString());
                lastUserId = Long.parseLong(userIdObj.toString());
            }
        }
        List<IndexUserListVo> users = userInfoMapper.getFansCountBasedUserList(lastFansCount, lastUserId, size+1);
        // 设置等级
        setLevel(users);
        // 构建PageResponse返回结果
        PageResponse<IndexUserListVo> response = new PageResponse<>();
        response.setPageSize(size);

        // 判断是否还有更多数据
        boolean hasNext = users.size() > size;
        response.setHasNext(hasNext);

        // 设置实际返回的数据列表
        if (hasNext) {
            // 移除多查的一个元素
            response.setContent(users.subList(0, size));
            // 生成下一个游标
            IndexUserListVo lastUser = users.get(size - 1);
            String nextCursor = generateFansCountCursor(lastUser);
            response.setNextCursor(nextCursor);
        } else {
            response.setContent(users);
        }
        return response;
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

    /**
     * 获取用户等级
     * @param exp
     * @return
     */
    public Integer getUserLevel(Integer exp) {
        AtomicReference<Integer> level = new AtomicReference<>(1);
        TreeMap<Integer, Integer> levelMap = ContentStartupConfig.Level_MAP;
        AtomicBoolean isFound = new AtomicBoolean(false);
        levelMap.forEach((singleLevel, expThreshold) -> {
            if (isFound.get()) {
                return;
            }
            if (exp < expThreshold) {
                isFound.set(true);
                return;
            }
            level.set(singleLevel);
        });
        return level.get();
    }

    /**
     * 获取用户博客年龄
     * @param joinTime
     * @return
     */
    public Integer getUserBlogAge(LocalDateTime joinTime) {
        return LocalDate.now().getYear() - joinTime.getYear();
    }

    public static Boolean checkEmail(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches( "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }

    /**
     * 解析游标字符串
     * @param cursor 游标字符串
     * @return 解析后的游标数据
     */
    private Map<String, Object> parseCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(decoded, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成游标字符串
     * @param user 当前用户
     * @return 游标字符串
     */
    private String generateCursor(IndexUserListVo user) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("score", user.getScore());
            cursorMap.put("userId", user.getUserId());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成基于文章数的游标字符串
     * @param user 当前用户
     * @return 游标字符串
     */
    private String generatePostCountCursor(IndexUserListVo user) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("postCount", user.getPostCount());
            cursorMap.put("userId", user.getUserId());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成基于经验值获取的游标字符串
     */
    private String generateExpCursor(IndexUserListVo user) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("exp", user.getExp());
            cursorMap.put("userId", user.getUserId());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据粉丝数获取游标字符串
     */
    private String generateFansCountCursor(IndexUserListVo user) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("fansCount", user.getFanCount());
            cursorMap.put("userId", user.getUserId());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 等级赋值
     */
    private void setLevel(List<IndexUserListVo> users) {
        users.forEach(user -> {
            user.setLevel(getUserLevel(user.getExp()));
        });
    }

}
