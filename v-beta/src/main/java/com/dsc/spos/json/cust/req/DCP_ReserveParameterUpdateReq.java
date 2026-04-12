package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import javax.persistence.PreUpdate;

/**
 * @description: 预约参数更新
 * @author: wangzyc
 * @create: 2021-07-23
 */
@Data
public class DCP_ReserveParameterUpdateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;              // 所属门店
        private String reserveAudit;        // 预约审核Y/N
        private String smsAlerts;           // 短信提醒Y/N
        private String shopDistribution;    // 预约到店分配Y/N
        private String appointmentDays;     // 最大预约天数
        private String shopAppointments;    // 到店分配最大预约数
        private String cancelType;          // 取消预约限制规则Y/N
        private String cancelTime;          // 取消预约时间限制(分钟)
    }
}
