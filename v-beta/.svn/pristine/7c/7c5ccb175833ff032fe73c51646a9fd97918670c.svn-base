package com.dsc.spos.utils.date;


import com.dsc.spos.utils.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author liyyd
 * @Date 20250227
 * @Descript: 用于开始日期和结束日期的拆分，传入新的日期时间段，将需要的日期拆分为几个连续的时间段
 */
public class DatePeriodSplitter {

    public static List<DatePeriod> splitIntoContinuousPeriods(List<DatePeriod> periods, Date newBeginDate, Date newEndDate) {
        if (periods == null || periods.isEmpty()) {
            return new ArrayList<>();
        }

        List<DatePeriod> result = new ArrayList<>();

        boolean toAdd = false;

        for (DatePeriod period : periods) {
            toAdd = false;
            // 如果当前时间段与调价时间段没有重叠，直接加入结果
            if (period.getEndDate().before(newBeginDate) || period.getStartDate().after(newEndDate)) {
                result.add(period);
            } else {
                // 如果有重叠，拆分时间段
                // 1. 加入调价时间段之前的部分
                if (period.getStartDate().before(newBeginDate)) {
                    result.add(new DatePeriod(period.getId(), period.getStartDate(), DateFormatUtils.addDay(newBeginDate, -1)));
                    toAdd = true;
                }

                // 2. 加入调价时间段之后的部分
                if (period.getEndDate().after(newEndDate)) {
                    //原始的被占用则需要用新增的方式返回
                    if (toAdd) {
                        result.add(new DatePeriod(period.getId() * -1, DateFormatUtils.addDay(newEndDate, 1), period.getEndDate()));
                    } else {
                        result.add(new DatePeriod(period.getId(), DateFormatUtils.addDay(newEndDate, 1), period.getEndDate()));
                    }
                }
            }
        }
        // 加入新的调价时间段
        result.add(new DatePeriod(0, newBeginDate, newEndDate));


        return result;

    }


}
