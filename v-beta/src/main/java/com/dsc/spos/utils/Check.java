package com.dsc.spos.utils;

import java.util.regex.Pattern;

public class Check {

    public static boolean Null(String a) {
        return a == null || a.length() <= 0;
    }

    public static boolean NotNull(String a) {
        return !Null(a);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence a) {
        return !isEmpty(a);
    }

    /**
     * 是否为合法IP地址
     *
     * @param ip
     * @return
     */
    public static boolean isIP(String ip) {
        Pattern pattern = Pattern.compile("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$");
        return pattern.matcher(ip).matches();
    }

    /**
     * 判断是否为整型数据
     *
     * @param d
     */
    public static boolean isInteger(double d) {
        try {
            return d % 1 == 0;
        } catch (Exception e) {
            return false;
        }
    }


};


