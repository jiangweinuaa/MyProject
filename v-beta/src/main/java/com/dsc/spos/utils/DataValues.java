package com.dsc.spos.utils;


import com.dsc.spos.dao.DataValue;

import java.sql.Types;

/**
 * DataValue工具类
 */
public class DataValues {

    /**
     * null 字符传入时不做处理，写入数据库null
     */
    public static DataValue newStringNull(Object value) {
        if (value == null){
            return newNull();
        }
        return new DataValue(value, Types.VARCHAR);
    }

    public static DataValue newString(Object value) {
        if (null == value) {
            value = "";
        }
        return new DataValue(value, Types.VARCHAR);
    }

    public static DataValue newDate(Object value) {
        //日期型不能直接给空串，会导致数据库给一笔默认，因此这里如果是空，则直接返回null类型用作替换
        if (value == null || StringUtils.isEmpty(value.toString())) {
            return new DataValue(null, Types.DATE);
        }
        String newDate = DateFormatUtils.getDateTime(String.valueOf(value));
        if (StringUtils.isEmpty(newDate)){
            return new DataValue(null, Types.DATE); //20241015 add by 11217 判断是否空串，如果空串给null
        }
        return new DataValue(newDate, Types.DATE);
    }

    public static DataValue newInteger(Object value) {
        if (StringUtils.isEmpty(StringUtils.toString(value,""))) { //20250402 增加控制空串，直接给null，防止无法插入数据库的问题
          return newNull();
        }
        return new DataValue(value, Types.INTEGER);
    }

    public static DataValue newDecimal(Object value) {
        if (StringUtils.isEmpty(StringUtils.toString(value,""))) {//20250402 增加控制空串，直接给null，防止无法插入数据库的问题
            return newNull();
        }
        return new DataValue(value, Types.DECIMAL);
    }

    public static DataValue newNull(Object value) {
        return new DataValue(null, Types.NULL);
    }

    public static DataValue newNull() {
        return new DataValue(null, Types.NULL);
    }

}
