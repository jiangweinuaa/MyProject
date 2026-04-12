package com.dsc.spos.utils.date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DatePeriod {
    //给定id的为原始数据
    private int id;
    //开始日期
    private Date startDate;
    //结束日期
    private Date endDate;

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return id + ":[" + sdf.format(startDate) + " - " + sdf.format(endDate) + "]";
    }

}
