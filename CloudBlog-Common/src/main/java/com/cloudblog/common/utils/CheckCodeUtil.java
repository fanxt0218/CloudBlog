package com.cloudblog.common.utils;

import com.cloudblog.common.enums.CheckCodeTargetType;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码工具类（暂时默认实现）
 */
@Slf4j
public class CheckCodeUtil {

    /**
     * 手机验证码
     */
    static ConcurrentHashMap<String, String> phoneCheckCode = new ConcurrentHashMap<>();

    /**
     * 邮箱验证码
     */
    static ConcurrentHashMap<String, String> emailCheckCode = new ConcurrentHashMap<>();

    /**
     * 生成验证码
     * @param target 验证码目标
     * @param type 验证码类型
     */
    public static void generateCheckCode(String target, String type) {
        if (CheckCodeTargetType.PHONE.getValue().equals(type)) {
            generatePhoneCheckCode(target);
        } else if (CheckCodeTargetType.EMAIL.getValue().equals(type)) {
            generateEmailCheckCode(target);
        }
    }

    /**
     * 生成手机验证码
     * @param phone 手机号
     */
    public static void generatePhoneCheckCode(String phone) {
        //生成4位数字验证码
        String checkCode = generateNumericCode(4);
        phoneCheckCode.put(phone, checkCode);
        log.info("手机:{}, 验证码：{}",phone, checkCode);
    }

    /**
     * 生成邮箱验证码
     * @param email 邮箱
     * @return 手机验证码
     */
    public static void generateEmailCheckCode(String email) {
        // 生成6位验证码
        String checkCode = generateNumericCode(6);
        emailCheckCode.put(email, checkCode);
        log.info("邮箱:{}, 验证码：{}",email, checkCode);
    }

    /**
     * 验证验证码
     * @param target 目标
     * @param checkCode 验证码
     * @param type 类型
     * @return 是否存在
     */
    public static boolean checkCheckCode(String target, String checkCode, String type) {
        if (CheckCodeTargetType.PHONE.getValue().equals(type)) {
            String code = phoneCheckCode.get(target);
            return code != null && code.equals(checkCode);
        } else if (CheckCodeTargetType.EMAIL.getValue().equals(type)) {
            String code = emailCheckCode.get(target);
            return code != null && code.equals(checkCode);
        }
        return false;
    }

    /**
     * 生成指定位数的数字验证码
     * @param digits 验证码位数
     * @return 指定位数的数字验证码
     */
    public static String generateNumericCode(int digits) {
        if (digits <= 0) {
            throw new IllegalArgumentException("验证码位数必须大于0");
        }

        Random random = new Random();
        StringBuilder code = new StringBuilder();

        // 第一位不能为0，避免出现前导零的情况
        code.append(random.nextInt(9) + 1);

        // 剩余位可以是0-9任意数字
        for (int i = 1; i < digits; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }
}
