package com.cloudblog.common.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // 默认的计算成本因子，值越高越安全但越慢(4-31)
    private static final int DEFAULT_COST = 12;

    /**
     * 加密密码
     * @param plainPassword 明文密码
     * @return 加密后的哈希值
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(DEFAULT_COST));
    }

    /**
     * 加密密码（自定义成本因子）
     * @param plainPassword 明文密码
     * @param cost 成本因子(4-31)
     * @return 加密后的哈希值
     */
    public static String hashPassword(String plainPassword, int cost) {
        if (cost < 4 || cost > 31) {
            throw new IllegalArgumentException("Cost factor must be between 4 and 31");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(cost));
    }

    /**
     * 验证密码
     * @param plainPassword 待验证的明文密码
     * @param hashedPassword 存储的哈希密码
     * @return 验证结果
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * 判断密码是否需要重新哈希（当成本因子提高时）
     * @param hashedPassword 存储的哈希密码
     * @return 是否需要重新哈希
     */
    public static boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return true;
        }

        // 提取当前的成本因子
        int currentCost;
        try {
            currentCost = Integer.parseInt(hashedPassword.substring(4, 6));
        } catch (NumberFormatException e) {
            return true;
        }

        // 如果当前成本因子低于默认值，则需要重新哈希
        return currentCost < DEFAULT_COST;
    }
}
