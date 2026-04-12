package com.dsc.spos.utils;

import com.dsc.spos.dao.DataValue;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * ColumnDataValues的扩展类，
 * 用于相同的ColumnDataValues的合并转换等
 */
public class ColumnDataValueExpend {

    private final List<ColumnDataValue> samecolumnList = new ArrayList<>();

    /*
     * 添加具有相同了列名的对象
     */
    public void addWithSameColumn(@NotNull ColumnDataValue columnDataValue) {
        if (samecolumnList.isEmpty()) {
            samecolumnList.add(columnDataValue);
        } else {
            String[] oColumns = samecolumnList.get(0).getColumnsArray();
            String[] tColumns = columnDataValue.getColumnsArray();
            if (Arrays.equals(oColumns, tColumns)) {
                samecolumnList.add(columnDataValue);
            }
        }
    }

    public void clearSameColumnList() {
        samecolumnList.clear();
    }

    private String getUniqueValue(@NotNull String[] uniqueColumn, @NotNull ColumnDataValue columnDataValue) {
        StringBuilder uniqueValue = new StringBuilder();
        for (String s : uniqueColumn) {
            DataValue data = columnDataValue.getDataValue(s);
            if (null != data) {
                uniqueValue.append(data.getValue());
            }
        }
        return uniqueValue.toString();
    }

    /*
     * 根据传入的字段名合并字段并返回
     */
    public List<ColumnDataValue> combineSum(String[] uniqueColumn, String[] sumColumn) {
        return combineSumDirection(null, uniqueColumn, sumColumn);
    }

    /*
       有方向字段的合并，会根据方向字段将数值型改变
       方向字段的值必须为1或-1
     */
    public List<ColumnDataValue> combineSumDirection(String directionColumn, String[] uniqueColumn, String[] sumColumn) {

        Map<String, ColumnDataValue> uniqueValueMap = new HashMap<>();

        for (ColumnDataValue columnDataValue : samecolumnList) {
            int dir = getDir(directionColumn, columnDataValue);
            String unique = getUniqueValue(uniqueColumn, columnDataValue);
            ColumnDataValue newColumnDataValue = new ColumnDataValue();
            if (null == uniqueValueMap.get(unique)) {
                //添加唯一字段
                for (String s : uniqueColumn) {
                    newColumnDataValue.add(s, columnDataValue.getDataValue(s));
                }
                //添加合并字段
                for (String s : sumColumn) {
                    newColumnDataValue.add(s, columnDataValue.getDataValue(s));
                }
                //添加方向字段
                if (StringUtils.isNotEmpty(directionColumn)) {
                    newColumnDataValue.add(directionColumn, columnDataValue.getDataValue(directionColumn));
                }
                //合并
                uniqueValueMap.put(unique, newColumnDataValue);
            } else {
                newColumnDataValue = uniqueValueMap.get(unique);
                for (String s : sumColumn) {
                    DataValue di = newColumnDataValue.getDataValue(s);
                    DataValue d2 = columnDataValue.getDataValue(s);
                    DataValue newData = tryCombineValue(dir, di, d2);
                    newColumnDataValue.add(s, newData);
                    if (newData.getValue() instanceof Double && StringUtils.isNotEmpty(directionColumn)) {
                        int newDir = Double.parseDouble(newData.getValue().toString()) >= 0 ? 1 : -1;
                        if (newDir != dir) {
                            newColumnDataValue.add(directionColumn, DataValues.newInteger(dir));
                        }
                    }
                }
            }
        }

        return new ArrayList<>(uniqueValueMap.values());
    }


    private DataValue tryCombineValue(int dir, DataValue oData, DataValue newData) {

        try {
            double d1 = Double.parseDouble(oData.getValue().toString());
            double d2 = Double.parseDouble(newData.getValue().toString()) * dir;
            oData.setValue(d1 + d2);

        } catch (Exception e) {
            //doNothing
        }
        return oData;
    }

    private int getDir(String directionColumn, ColumnDataValue columnDataValue) {
        int dir = 1;
        try {
            if (StringUtils.isNotEmpty(directionColumn)) {
                dir = Integer.parseInt(columnDataValue.getDataValue(directionColumn).getValue().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("请检查方向字段赋值 需要是1或-1");
        }
        return dir;
    }


}
