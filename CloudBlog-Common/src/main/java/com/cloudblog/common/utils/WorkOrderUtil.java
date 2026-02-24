package com.cloudblog.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * 工单号生成工具类
 * 生成32位的工单号
 */
public class WorkOrderUtil {

    private static final String WORK_ORDER_PREFIX = "WO"; // 工单前缀
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成32位工单号
     * 格式：WO + 日期时间(14位) + 随机数(16位)
     * @return 32位工单号
     */
    public static String generateWorkOrderNumber() {
        // 前缀 WO (2位)
        StringBuilder sb = new StringBuilder(WORK_ORDER_PREFIX);
        
        // 日期时间 yyyyMMddHHmmss (14位)
        String dateTime = LocalDateTime.now().format(DATE_FORMATTER);
        sb.append(dateTime);
        
        // 随机数部分 (16位)
        String randomPart = generateRandomString(16);
        sb.append(randomPart);
        
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字符串(包含数字和大写字母)
     * @param length 长度
     * @return 随机字符串
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }

    /**
     * 基于UUID生成32位工单号(去除连接符)
     * @return 32位工单号
     */
    public static String generateWorkOrderNumberByUUID() {
        return WORK_ORDER_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(2);
    }

    /**
     * 生成带业务类型标识的工单号
     * @param businessType 业务类型标识(2位)
     * @return 32位工单号
     */
    public static String generateWorkOrderNumberWithBusinessType(String businessType) {
        if (businessType == null || businessType.length() != 2) {
            throw new IllegalArgumentException("业务类型标识必须为2位字符");
        }
        
        StringBuilder sb = new StringBuilder(WORK_ORDER_PREFIX);
        sb.append(businessType); // 业务类型(2位)
        
        // 日期时间 yyyyMMddHHmmss (14位)
        String dateTime = LocalDateTime.now().format(DATE_FORMATTER);
        sb.append(dateTime);
        
        // 随机数部分 (14位)
        String randomPart = generateRandomString(14);
        sb.append(randomPart);
        
        return sb.toString();
    }

    /**
     * 验证工单号格式是否正确
     * @param workOrderNumber 工单号
     * @return 是否有效
     */
    public static boolean isValidWorkOrderNumber(String workOrderNumber) {
        if (workOrderNumber == null || workOrderNumber.length() != 32) {
            return false;
        }
        
        // 检查前缀
        if (!workOrderNumber.startsWith(WORK_ORDER_PREFIX)) {
            return false;
        }
        
        // 检查是否只包含允许的字符
        return workOrderNumber.matches("^WO[0-9A-Z]+$");
    }
}
