package com.dsc.spos.utils;

import com.dsc.spos.dao.DataValue;
import lombok.Getter;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date 20240918
 * @Author liyyd
 * 字段和值的映射工具类
 * 替换com/dsc/spos/ninetyone/util/ColumnDataValue.java
 */
public class ColumnDataValue {

    private int size;

    @Getter
    private List<String> columns;

    @Getter
    private List<DataValue> dataValues;

    public ColumnDataValue() {
        size = 0;
        columns = new ArrayList<>();
        dataValues = new ArrayList<>();
    }

    //原方法逻辑
    public boolean add(String columnName, Object value, int dataType) {
        return add(columnName, new DataValue(value, dataType));
    }

    //增加直接给String的方法
    public boolean add(String columnName, String dataValue) {
        return add(columnName, dataValue, Types.VARCHAR);
    }

    //增加直接传入值的方法
    public boolean add(String columnName, DataValue dataValue) {
        //20250515 modi by 11217 如果为null 则不需要写数据，防止有需要null值的字段被改写
        if (null == dataValue) {
            return false;
        }
        if (dataValue.getDataType() != Types.DATE && null == dataValue.getValue()){
            return false;
        }

        if (columns.contains(columnName)) {
            int i = columns.indexOf(columnName);
            dataValues.remove(i);
            dataValues.add(i, dataValue);
        } else {
            columns.add(size, columnName);
            dataValues.add(size, dataValue);
            size++;
        }
        return true;
    }

    //增加链式调用
    public ColumnDataValue append(String columnName, DataValue dataValue) {
        add(columnName, dataValue);
        return this;
    }

    //增加返回array的调用
    public String[] getColumnsArray() {
        return columns.toArray(new String[0]);
    }

    //增加返回array的调用
    public DataValue[] getDataValuesArray() {
        return dataValues.toArray(new DataValue[0]);
    }

    //增加根据列名返回具体字段的方法
    public DataValue getDataValue(String columnName) {
        int i = columns.indexOf(columnName);
        if (i >= 0) {
            return dataValues.get(i);
        }
        return null;
    }

    public int size() {
        return size;
    }

    public ColumnDataValue copyNew() {
        ColumnDataValue columnDataValue = new ColumnDataValue();
        for (int i = 0; i < this.size; i++) {
            columnDataValue.add(this.getColumns().get(i), this.getDataValues().get(i));
        }
        return columnDataValue;
    }

    public void copyTo(ColumnDataValue target) {
        if (null == target) {
            target = new ColumnDataValue();
        }
        for (int i = 0; i < this.size; i++) {
            target.add(this.getColumns().get(i), this.getDataValues().get(i));
        }
    }

}
