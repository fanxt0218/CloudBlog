package com.cloudblog.user.config;

import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Vo.IndexUserListVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@Order(1)
public class UserStartupConfig implements ApplicationRunner {

    public static final HashMap<Long, Long> USER_RANKING_MAP = new HashMap<>();

    private final UserInfoService userInfoService;

    public UserStartupConfig(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("加载用户排名信息");
        loadUserRanking();
    }

    private void loadUserRanking() {
        Long usersCount = userInfoService.getUsersCount();
        AjaxResult indexUserList = userInfoService.getIndexUserList(null, Math.toIntExact(usersCount), null);
        PageResponse<IndexUserListVo> data = (PageResponse<IndexUserListVo>) indexUserList.get("data");
        data.getContent().forEach(user -> {
            USER_RANKING_MAP.put(user.getUserId(), user.getRankNum());
        });
    }
}
