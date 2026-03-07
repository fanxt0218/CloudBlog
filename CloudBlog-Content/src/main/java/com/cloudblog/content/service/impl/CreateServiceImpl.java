package com.cloudblog.content.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.Po.InteractionTrendPo;
import com.cloudblog.common.pojo.Vo.InteractionTrendVo;
import com.cloudblog.common.pojo.Vo.UserCreatorCenterListVo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.UploadUtil;
import com.cloudblog.content.mapper.CreateMapper;
import com.cloudblog.content.service.CommentService;
import com.cloudblog.content.service.CreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CreateServiceImpl implements CreateService {

    private final String UploadImagePath = "/contentFile/image";

    private final String UploadVideoPath = "/contentFile/video";

    @Autowired
    private CreateMapper createMapper;
    @Autowired
    private CommentService commentService;

    @Override
    public AjaxResult uploadImage(MultipartFile file) {
        String path = UploadUtil.uploadFile(file, UploadImagePath);
        return AjaxResult.success("上传成功", path);
    }

    @Override
    public AjaxResult uploadVideo(MultipartFile file) {
        String path = UploadUtil.uploadFile(file, UploadVideoPath);
        return AjaxResult.success("上传成功", path);
    }

    @Override
    public AjaxResult interactionTrend(InteractionTrendPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        // 处理时间参数默认值
        LocalDate endDate = po.getEndDate();
        LocalDate startDate = po.getStartDate();

        // 如果没有传入结束时间，默认为当天
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 如果没有传入开始时间，默认为结束时间的 7 天前
        if (startDate == null) {
            startDate = endDate.minusDays(7);
        }

        // 设置回参对象
        po.setStartDate(startDate);
        po.setEndDate(endDate);

        // TODO 先查缓存
        List<InteractionTrendVo> interactionTrendVos = new ArrayList<>();
        // 查询点赞趋势
        List<InteractionTrendVo.ContentTrend> likeTrend = createMapper.getLikeTrend(po);
        interactionTrendVos.add(new InteractionTrendVo("like", likeTrend));
        // 获取收藏趋势
        List<InteractionTrendVo.ContentTrend> collectTrend = createMapper.getCollectTrend(po);
        interactionTrendVos.add(new InteractionTrendVo("collect", collectTrend));
        // 获取评论趋势
        List<InteractionTrendVo.ContentTrend> commentTrend = createMapper.getCommentTrend(po);
        interactionTrendVos.add(new InteractionTrendVo("comment", commentTrend));

        return AjaxResult.success("查询成功", interactionTrendVos);
    }

    @Override
    public AjaxResult fanTrend(InteractionTrendPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }

        // 处理时间参数默认值
        LocalDate endDate = po.getEndDate();
        LocalDate startDate = po.getStartDate();

        // 如果没有传入结束时间，默认为当天
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 如果没有传入开始时间，默认为结束时间的 7 天前
        if (startDate == null) {
            startDate = endDate.minusDays(7);
        }

        // 设置回参对象
        po.setStartDate(startDate);
        po.setEndDate(endDate);

        // TODO 先查缓存
        List<InteractionTrendVo.ContentTrend> fanTrend = createMapper.getFanTrend(po);
        InteractionTrendVo fan = new InteractionTrendVo("fan", fanTrend);
        return AjaxResult.success("查询成功", fan);
    }

    @Override
    public AjaxResult getCreateContentList(Long userId, Integer type, Integer pageNum, Integer pageSize) {
        pageSize = pageSize == null || pageSize <= 0 ? 10 : pageSize;
        pageNum = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (type == null) {
            // 默认是正常内容
            type = 2;
        }
        Page<UserCreatorCenterListVo> page = new Page<>(pageNum, pageSize);
        IPage<UserCreatorCenterListVo> contentList = createMapper.getContentList(page, userId, type);
        contentList.getRecords().forEach(content -> {
            content.setCommentCount(commentService.getUserCommentCount(content.getId()));
        });
        return AjaxResult.success("查询成功", contentList);
    }
}
