package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约参数查询
 * @author: wangzyc
 * @create: 2021-07-23
 */
@Data
public class DCP_ReserveParameterQueryRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String reserveAudit;        // 预约审核Y/N
        private String smsAlerts;           // 短信提醒Y/N
        private String shopDistribution;    // 预约到店分配Y/N
        private String appointmentDays;     // 最大预约天数 默认1,非数字字符时返回1
        private String shopAppointments;    // 到店分配最大预约数  默认1,非数字字符时返回1
        private String cancelType;          // 取消预约限制规则Y/N
        private String cancelTime;          // 取消预约时间限制(分钟)
    }
}
