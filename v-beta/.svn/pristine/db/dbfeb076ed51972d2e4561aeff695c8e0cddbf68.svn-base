package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 顾问新增
 * @author: wangzyc
 * @create: 2021-07-14
 */
@Data
public class DCP_AdvisorCreateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String opNo;         // 员工编号
        private String opName;       // 员工名称
        private String headImageName;    // 头像
        private String professionalId;// 职称代号
        private String ability;      // 能力介绍
        private String status;       // 状态
        private List<level2Elm> schedulingList;     // 排班时间段
        private List<level3Elm> restTimeList;       // 员工休息时间
    }

    @Data
    public class level2Elm{
        private String shopId;      // 门店编号
        private String item;       // 项次
        private String cycle;      // 有效周期，{1,2,3,4...}
        private String timeInterval;      // 时间段，09:00-10:00
        private String appointments;      // 最大预约数
    }

    @Data
    public class level3Elm{
        private String shopId;       // 门店编号
        private String item;         // 项次
        private String cycleType;      // 周期类型，month每月 custom自定义
        private String restTime;      // 休息日期yyyy-MM-dd或dd
        private String status;        // 状态
    }
}
