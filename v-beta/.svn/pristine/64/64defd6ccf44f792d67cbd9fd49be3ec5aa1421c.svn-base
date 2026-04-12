package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约时间查询
 * @author: wangzyc
 * @create: 2021-08-02
 */
@Data
public class DCP_ReserveTimeQuery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm {
        private List<level2Elm> dateList; // 可选日期范围
        private List<level3Elm> timeList; // 预约时间段
    }

    @Data
    public class level2Elm {
        private String date; // 可选日期07.01
        private String week; // 周几
    }

    @Data
    public class level3Elm {
        private String beginTime; // 开始时间12:00
        private String endTime; // 结束时间13:00
        private String disabled; // 有值即不可选 1.超过当前时间，（前端需置灰） 2.商家休息中 3.剩余时间不足服务时长 4.超过项目老师预约上线
    }
}
