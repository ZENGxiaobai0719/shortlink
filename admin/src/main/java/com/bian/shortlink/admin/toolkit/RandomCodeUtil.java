package com.bian.shortlink.admin.toolkit;

import java.util.Random;

/**
 * 随机码工具类
 * 生成 6 位包含【数字 + 大小写字母】的随机字符串
 */
public final class RandomCodeUtil {

    // 随机字符源：数字 + 大写字母 + 小写字母
    private static final String CHAR_POOL = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    /**
     * 生成 6 位随机码（数字+字母混合）
     *
     * @return 6 位随机字符串
     */
    public static String generateSixDigitCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            // 从字符池中随机取一个字符
            int index = RANDOM.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }

        return sb.toString();
    }
}