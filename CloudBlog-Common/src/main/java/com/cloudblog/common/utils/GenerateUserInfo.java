package com.cloudblog.common.utils;

import java.util.Random;

public class GenerateUserInfo {

    /**
     * 生成用户账号
     * @return
     */
    public static String generateUserAccount() {
        // 前缀
        String prefix = "CBL";
        // 时间戳
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 随机数
        String random = String.valueOf(new Random().nextInt(1000));
        return prefix + timestamp + random;
    }
}
