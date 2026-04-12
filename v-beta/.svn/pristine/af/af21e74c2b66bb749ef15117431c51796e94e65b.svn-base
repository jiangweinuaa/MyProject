package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约单修改
 * @author: wangzyc
 * @create: 2021-08-03
 */
@Data
public class DCP_ReserveOrderUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String reserveNo; // 预约单号
        private String opNo; // 顾问编号
        private String date; // 预约时段
        private String time; // 预约时间段
        private String memo; // 捎话
    }
}
