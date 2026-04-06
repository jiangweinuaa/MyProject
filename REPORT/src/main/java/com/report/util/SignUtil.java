package com.report.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;

import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 * 支持 MD5 签名和 AES 加解密
 */
public class SignUtil {

    /**
     * 生成 MD5 签名
     * @param content 待签名内容
     * @param key 密钥
     * @return 签名值 (32 位小写)
     */
    public static String generateSign(String content, String key) {
        String raw = content + key;
        return DigestUtil.md5Hex(raw);
    }

    /**
     * 验证签名
     * @param content 待验证内容
     * @param key 密钥
     * @param sign 签名值
     * @return 是否匹配
     */
    public static boolean verifySign(String content, String key, String sign) {
        String expected = generateSign(content, key);
        return expected.equalsIgnoreCase(sign);
    }

    /**
     * 对 Map 参数进行排序并生成签名
     * @param params 参数 Map
     * @param key 密钥
     * @return 签名值
     */
    public static String generateSign(Map<String, Object> params, String key) {
        // 按 key 排序
        Map<String, Object> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        // 去掉最后一个&
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return generateSign(sb.toString(), key);
    }

    /**
     * AES 加密
     * @param content 明文
     * @param key 密钥 (16/24/32 字节)
     * @return 密文 (Base64)
     */
    public static String aesEncrypt(String content, String key) {
        AES aes = new AES(key.getBytes());
        return aes.encryptBase64(content);
    }

    /**
     * AES 解密
     * @param content 密文 (Base64)
     * @param key 密钥 (16/24/32 字节)
     * @return 明文
     */
    public static String aesDecrypt(String content, String key) {
        AES aes = new AES(key.getBytes());
        return aes.decryptStr(content);
    }
}
