package com.dsc.spos.utils;

public class StringUtil {
    /**
     * 16进制转换成byte数组
     */
    public static byte[] hex2byte(byte[] hex) {
        if ((hex.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }

        byte[] result = new byte[hex.length / 2];
        for (int i = 0; i < hex.length; i += 2) {
            String s = new String(hex, i, 2);
            result[i / 2] = (byte) Integer.parseInt(s, 16);
        }
        return result;
    }



    /**
     * byte数组转换成16进制串
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String s = Integer.toHexString(b & 0XFF);
            if (s.length() == 1) {
                stringBuilder.append("0").append(s);
            } else {
                stringBuilder.append(s);
            }
        }
        return stringBuilder.toString().toUpperCase();
    }
}
