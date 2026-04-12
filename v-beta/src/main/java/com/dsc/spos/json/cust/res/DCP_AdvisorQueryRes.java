package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_AdvisorCreateReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 顾问查询
 * @author: wangzyc
 * @create: 2021-07-19
 */
@Data
public class DCP_AdvisorQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String opNo;         // 员工编号
        private String opName;       // 员工名称
        private String headImageUrl;    // 头像链接
        private String headImageName;   // 头像
        private String professionalId;// 职称代号
        private String professionalName;// 职称名称
        private String ability;      // 能力介绍
        private String status;       // 状态
        private List<level2Elm> schedulingList;     // 排班时间段
        private List<level3Elm> restTimeList;       // 员工休息时间
    }

    @Data
    public class level2Elm{
        private String shopId;      // 门店编号
        private String cycle;      // 有效周期，[1,2,3,4...]
        private String item;      // 项次
        private String timeInterval;      // 时间段，09:00-10:00
        private String appointments;      // 最大预约数
    }

    @Data
    public class level3Elm{
        private String shopId;       // 门店编号
        private String cycleType;      // 周期类型，month每月 custom自定义
        private String item;      // 项次
        private String restTime;      // 休息日期yyyy-MM-dd或dd
        private String status;        // 状态
    }
}
