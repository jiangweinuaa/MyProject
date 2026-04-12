package com.dsc.spos.utils;

import com.dsc.spos.dao.*;
import com.google.common.collect.Maps;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


/**
 * @author liyyd
 * 用于数据库执行Bean类的创建
 */
public class DataBeans {

    public static InsBean getInsBean(String tableName, String[] columnsName, DataValue[] dataValues) throws RuntimeException {
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("表名不可为空");
        if (null == columnsName || columnsName.length <= 0) throw new RuntimeException("请传入增加的字段名");
        InsBean insBean = new InsBean(tableName, columnsName);
        if (null == dataValues || dataValues.length <= 0) return insBean;
        insBean.addValues(dataValues);
        return insBean;
    }

    public static InsBean getInsBean(String tableName, @NotNull ColumnDataValue data) throws RuntimeException {
        if (null == data) throw new RuntimeException("请传入增加的值");
        return getInsBean(tableName, data.getColumnsArray(), data.getDataValuesArray());
    }

    public static DataProcessBean getInsDataBean(String tableName, @NotNull ColumnDataValue data) throws RuntimeException {
        return new DataProcessBean(getInsBean(tableName, data.getColumnsArray(), data.getDataValuesArray()));
    }

    public static DelBean getDelBean(String tableName, String[] columnsName, DataValue[] dataValues) throws RuntimeException {
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("表名不可为空");
        if (null == columnsName || columnsName.length <= 0) throw new RuntimeException("请传入删除条件字段");
        if (null == dataValues || dataValues.length <= 0) throw new RuntimeException("请传入删除条件的值");
        if (columnsName.length != dataValues.length) throw new RuntimeException("删除条件字段和值不匹配");
        DelBean delBean = new DelBean(tableName);
        for (int i = 0; i < columnsName.length; i++) {
            delBean.addCondition(columnsName[i], dataValues[i]);
        }
        return delBean;
    }

    public static DelBean getDelBean(String tableName, @NotNull ColumnDataValue conditions) throws RuntimeException {
        if (null == conditions) throw new RuntimeException("请传入删除条件");
        return getDelBean(tableName, conditions.getColumnsArray(), conditions.getDataValuesArray());
    }

    public static DataProcessBean getDelDataBean(String tableName, @NotNull ColumnDataValue conditions) throws RuntimeException {
        return new DataProcessBean(getDelBean(tableName, conditions));
    }

    public static UptBean getUptBean(String tableName, String[] conditionColumns, String[] dataColumns, DataValue[] conditionData, DataValue[] valueData) throws RuntimeException {
        if (StringUtils.isEmpty(tableName)) throw new RuntimeException("表名不可为空");
        if (null == conditionColumns || conditionColumns.length <= 0) throw new RuntimeException("请传入修改条件字段");
        if (null == conditionData || conditionData.length <= 0) throw new RuntimeException("请传入修改条件的值");
        if (conditionColumns.length != conditionData.length) throw new RuntimeException("修改条件字段和值不匹配");
        if (null == dataColumns || dataColumns.length <= 0) throw new RuntimeException("请传入需要修改的字段");
        if (null == valueData || valueData.length <= 0) throw new RuntimeException("请传入需要修改的值");
        if (dataColumns.length != valueData.length) throw new RuntimeException("需要修改的字段和值不匹配");
        UptBean uptBean = new UptBean(tableName);
        for (int i = 0; i < conditionColumns.length; i++) {
            uptBean.addCondition(conditionColumns[i], conditionData[i]);
        }
        for (int i = 0; i < dataColumns.length; i++) {
            uptBean.addUpdateValue(dataColumns[i], valueData[i]);
        }

        return uptBean;
    }

    public static UptBean getUptBean(String tableName, @NotNull ColumnDataValue conditions, @NotNull ColumnDataValue data) {
        if (null == conditions) throw new RuntimeException("请传入修改条件");
        if (null == data) throw new RuntimeException("请传入需要修改的字段值");
        return getUptBean(tableName, conditions.getColumnsArray(), data.getColumnsArray(), conditions.getDataValuesArray(), data.getDataValuesArray());

    }

    public static DataProcessBean getUptDataBean(String tableName, @NotNull ColumnDataValue conditions, @NotNull ColumnDataValue data) {
        return new DataProcessBean(getUptBean(tableName, conditions, data));
    }

    public static ProcedureBean getProcedureBean(String procedureName, List<Object> dataValues) throws RuntimeException {
        if (null == dataValues) throw new RuntimeException("请传入存储过程参数值");
        Map<Integer, Object> input = Maps.newHashMap();
        for (int i = 0; i < dataValues.size(); i++) {
            input.put(i + 1, dataValues.get(i));
        }
        return new ProcedureBean(procedureName, input);
    }

    public static ProcedureBean getProcedureBean(String procedureName, @NotNull ColumnDataValue data) throws RuntimeException {
        if (null == data) throw new RuntimeException("请传入存储过程参数值");
        Map<Integer, Object> input = Maps.newHashMap();
        for (int i = 0; i < data.getDataValues().size(); i++) {
            input.put(i + 1, data.getDataValues().get(i).getValue());
        }
        return new ProcedureBean(procedureName, input);
    }

    public static DataProcessBean getProcedureDataBean(String procedureName, @NotNull ColumnDataValue data) throws RuntimeException {
        return new DataProcessBean(getProcedureBean(procedureName, data));
    }

    public static ExecBean getExecBean(String sql) throws RuntimeException {
        return new ExecBean(sql);
    }

    public static DataProcessBean getExecDataBean(String sql) throws RuntimeException {
        return new DataProcessBean(getExecBean(sql));
    }
}
