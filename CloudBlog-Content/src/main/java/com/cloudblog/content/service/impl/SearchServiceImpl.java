package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.UserSearchHistory;
import com.cloudblog.common.pojo.Po.DeleteSearchHistoryPo;
import com.cloudblog.common.pojo.Po.FocusUserPo;
import com.cloudblog.common.pojo.Vo.HotSearchVo;
import com.cloudblog.common.pojo.Vo.SearchUserVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.SearchMapper;
import com.cloudblog.content.service.FocusService;
import com.cloudblog.content.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchMapper searchMapper;
    @Autowired
    private FocusService focusService;

    @Override
    public AjaxResult getSearchHistory(Long userId) {
        List<UserSearchHistory> userSearchHistory = searchMapper.getUserSearchHistory(userId);
        // 去重处理
        HashSet<String> containsKeywords = new HashSet<>();
        List<UserSearchHistory> res = new ArrayList<>();
        for (UserSearchHistory record : userSearchHistory) {
            if (res.size() >= 10) break;
            if (containsKeywords.contains(record.getKeyword().trim())) continue;
            res.add(record);
            containsKeywords.add(record.getKeyword().trim());
        }
        return AjaxResult.success(res);
    }

    @Override
    public AjaxResult addSearchRecord(UserSearchHistory po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        if (po.getKeyword() == null || po.getKeyword().isEmpty()) {
            return AjaxResult.error("搜索关键字不能为空");
        }
        po.setCreateTime(LocalDateTime.now());
        return searchMapper.addSearchRecord(po) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @Override
    public AjaxResult searchUser(Long userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return AjaxResult.error("搜索关键字不能为空");
        }
        List<SearchUserVo> users = searchMapper.searchUser(keyword.trim());

        // 判断是否关注
        if (userId != null) {
            for (SearchUserVo user : users) {
                AjaxResult followStatus = focusService.getFollowStatus(FocusUserPo.builder().userId(userId).focusUserId(user.getUserId()).build());
                user.setFollow((boolean)followStatus.get("data") ? 1 : 0);
            }
        } else {
            for (SearchUserVo user : users) {
                user.setFollow(0);
            }
        }
        return AjaxResult.success(users);
    }

    @Override
    public AjaxResult getHotSearch() {
        // 先假实现
        List<HotSearchVo> hotSearchVos = new ArrayList<>();
        Random random = new Random();
        int i = random.nextInt(3);
        if (i == 1) {
            hotSearchVos.add(new HotSearchVo("java操作es", 100));
            hotSearchVos.add(new HotSearchVo("矩阵螺旋矩阵", 98));
            hotSearchVos.add(new HotSearchVo("秒杀系统设计", 96));
            hotSearchVos.add(new HotSearchVo("nginx打不开", 95));
            hotSearchVos.add(new HotSearchVo("idea 指定pg驱动 jar", 94));
            hotSearchVos.add(new HotSearchVo("linux日常命令", 93));
            hotSearchVos.add(new HotSearchVo("系统镜像", 92));
            hotSearchVos.add(new HotSearchVo("mybatislog插件", 91));
            hotSearchVos.add(new HotSearchVo("minio开源吗", 90));
            hotSearchVos.add(new HotSearchVo("elasticsearch", 90));
        } else if (i == 2){
            hotSearchVos.add(new HotSearchVo("前端面试题", 100));
            hotSearchVos.add(new HotSearchVo("nginx打不开", 98));
            hotSearchVos.add(new HotSearchVo("Java部署ES", 96));
            hotSearchVos.add(new HotSearchVo("dify搭建工作流", 95));
            hotSearchVos.add(new HotSearchVo("antigravity授权回调没反应", 94));
            hotSearchVos.add(new HotSearchVo("linux常用命令大全", 93));
            hotSearchVos.add(new HotSearchVo("kibana使用", 92));
            hotSearchVos.add(new HotSearchVo("mobaxterm连接服务器", 91));
            hotSearchVos.add(new HotSearchVo("事务", 90));
            hotSearchVos.add(new HotSearchVo("安装node环境", 90));

        } else {
            hotSearchVos.add(new HotSearchVo("langchain课程", 100));
            hotSearchVos.add(new HotSearchVo("递归算法原理", 98));
            hotSearchVos.add(new HotSearchVo("ollama调用本地大模型", 96));
            hotSearchVos.add(new HotSearchVo("毕业设计题目大全", 95));
            hotSearchVos.add(new HotSearchVo("Java实现我的世界", 94));
            hotSearchVos.add(new HotSearchVo("openclaw部署教程", 93));
            hotSearchVos.add(new HotSearchVo("idea激活码", 92));
            hotSearchVos.add(new HotSearchVo("RAG工程", 91));
            hotSearchVos.add(new HotSearchVo("k8s原理", 90));
            hotSearchVos.add(new HotSearchVo("计算机网络入门", 90));
        }

        return AjaxResult.success(hotSearchVos);
    }

    @Override
    public AjaxResult deleteSearchRecord(DeleteSearchHistoryPo po) {
        if (po.getIds() == null || po.getIds().isEmpty()) {
            return AjaxResult.error("请选择要删除的搜索记录");
        }
        return searchMapper.deleteSearchRecord(po.getIds()) > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    @Override
    public AjaxResult clearUserSearchRecord(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        return searchMapper.clearUserSearchRecord(userId) > 0 ? AjaxResult.success("清空成功") : AjaxResult.error("清空失败");
    }
}
