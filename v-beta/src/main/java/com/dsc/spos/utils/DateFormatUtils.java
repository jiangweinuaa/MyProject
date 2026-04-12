package com.dsc.spos.utils;


import javax.validation.constraints.NotNull;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 11217 liyangyang
 * @date 20240930
 * 日期转换工具
 * 时间日期格式化工具类
 */
public class DateFormatUtils {

    /**
     * 年月日时分秒类型
     * 基本上返回的类型都是这种
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 保存时需要的时间日期字符格式
     */
    public static final String PRO_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 日期格式 年-月-日
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 无格式日期 年月日
     */
    public static final String PLAIN_DATE_FORMAT = "yyyyMMdd";

    /**
     * 时间格式 24时:分:秒
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 时间格式 24时分秒
     */
    public static final String PLAIN_TIME_FORMAT = "HHmmss";

    /**
     * 毫秒时间格式 24时:分:秒.毫秒
     */
    public static final String MILL_TIME_FORMAT = "HH:mm:ss.SSS";

    /**
     * 年月
     */
    public static final String YEAR_MONTH_FORMAT = "yyyyMM";

    /**
     * 时间戳格式年月日时分秒
     */
    public static final String PLAIN_DATETIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 时间戳格式年月日时分秒毫秒
     */
    private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";


    private static final DateUtils dateUtils = DateUtils.getInstance();

    public static String getNowDateTime() {
        return dateUtils.format(new Date(), DATETIME_FORMAT);
    }

    public static String getNowDate() {
        return dateUtils.format(new Date(), DATE_FORMAT);
    }

    public static String getTimestamp() {
        return dateUtils.format(new Date(), TIMESTAMP_FORMAT);
    }

    public static String getDate(String date) {
        return getDate(date, DATE_FORMAT);
    }

    public static String getNowPlainTime() {
        return dateUtils.format(new Date(), PLAIN_TIME_FORMAT);
    }

    public static String getNowPlainDate() {
        return dateUtils.format(new Date(), PLAIN_DATE_FORMAT);
    }

    public static String getNowPlainDatetime() {
        return dateUtils.format(new Date(), PLAIN_DATETIME_FORMAT);
    }

    public static String getPlainDate(String date) {
        String newDate = date.replaceAll("[^0-9]", "");

        if (StringUtils.isEmpty(newDate)) {
            return "";
        }
        StringBuilder dateBuilder = new StringBuilder(newDate);

        while (dateBuilder.length() < 8) {
            dateBuilder.append("0");
        }
        if (dateBuilder.length() > 8) {
            newDate = dateBuilder.substring(0, 8);
        } else {
            newDate = dateBuilder.toString();
        }
        return dateUtils.format(dateUtils.parse(newDate, PLAIN_DATE_FORMAT), PLAIN_DATE_FORMAT);
    }

    public static String getDate(String date, String format) {
        if (StringUtils.isEmpty(date)) {
            return "";
        }
        String newDate = getPlainDate(date);

        return dateUtils.format(dateUtils.parse(newDate, PLAIN_DATE_FORMAT), format);
    }

    public static String getPlainYearMonth(String date) {
        return getPlainDate(date).substring(0, 6);
    }

    public static String getPlainDatetime(String date) {

        String newDate = date.replaceAll("[^0-9]", "");

        if (StringUtils.isEmpty(newDate)) {
            return "";
        }
        StringBuilder dateBuilder = new StringBuilder(newDate);

        while (dateBuilder.length() < 14) {
            dateBuilder.append("0");
        }
        if (dateBuilder.length() > 14) {
            newDate = dateBuilder.substring(0, 14);
        } else {
            newDate = dateBuilder.toString();
        }
        return dateUtils.format(dateUtils.parse(newDate, PLAIN_DATETIME_FORMAT), PLAIN_DATETIME_FORMAT);
    }

    public static String getYearMonthLastDate(String year, String period) {
        YearMonth ym = YearMonth.of(Integer.parseInt(year), Integer.parseInt(period));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PLAIN_DATE_FORMAT);

        return ym.atEndOfMonth().format(formatter);
    }


    public static String getDateTime(String date) {
        date = getPlainDatetime(date);
        return getDateTime(date, DATETIME_FORMAT);
    }

    public static String getDateTime(String date, String format) {
        if (StringUtils.isEmpty(date)) {
            return ""; //20241015 modi by 11217 返回空串
        }
        String newDate = getPlainDatetime(date);
        return dateUtils.format(dateUtils.parse(newDate, PLAIN_DATETIME_FORMAT), format);
    }


    /**
     * 20241008 add by 11217 日期比较
     */
    public static boolean lessNowDate(@NotNull String date) {
        return compareDate(date, getNowDate()) < 0;
    }

    /**
     * 20241008 add by 11217 日期比较
     */
    public static boolean greaterNowDate(@NotNull String date) {
        return compareDate(date, getNowDate()) > 0;
    }

    /**
     * 20241008 add by 11217 日期比较
     */
    public static int compareDate(@NotNull String date1, @NotNull String date2) {
        String newDate1 = getPlainDate(date1);
        String newDate2 = getPlainDate(date2);

        return newDate1.compareTo(newDate2);
    }

    public static String addMonth(String date, int month) {
        Date newDate = dateUtils.parse(getPlainDate(date), PLAIN_DATE_FORMAT);
        return format(addMonth(newDate, month), DATE_FORMAT);
    }

    public static Date addMonth(Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }

    public static String addDay(String date, int days) {
        Date newDate = dateUtils.parse(getPlainDate(date), PLAIN_DATE_FORMAT);
        return format(addDay(newDate, days), DATE_FORMAT);
    }


    public static Date addDay(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days); // 减去一天
        return cal.getTime();
    }

    public static Date parseDate(String date, String dateFormat) {
        return DateUtils.getInstance().parse(date, dateFormat);
    }

    public static Date parseDate(String date) {
        return parseDate(getPlainDate(date), PLAIN_DATE_FORMAT);
    }

    public static String format(Date date, String format) {
        return DateUtils.getInstance().format(date, format);
    }

    public static String format(Date date) {
        return format(date, DATETIME_FORMAT);
    }


}
