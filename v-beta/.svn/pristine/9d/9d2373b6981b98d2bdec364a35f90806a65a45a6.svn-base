package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 顾问查询
 * @author: wangzyc
 * @create: 2021-08-02
 */
@Data
public class DCP_AdvisorQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm
    {
        private String shopId; //   所属门店
        private String itemsNo; // 项目编号
        private String date; // 预约日期
        private String beginTime; // 预约开始时间12:00，待定
        private String endTime; // 预约结束时间13:00
        private String labelId; // 标签编码
        private String isOnlyAdvisor; //是否只查顾问，不查评分、排班情况  Y/N
        private String advisorNo; // 顾问编码，不传是查门店所有顾问

    }
}
