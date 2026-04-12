package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @description: KDS基础设置修改
 * @author: wangzyc
 * @create: 2021-09-22
 */
@Data
public class DCP_KdsBaseSetUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private String overTime; // 超出预警时间
        private String miniSendMsg; // 小程序取餐提醒
    }
}
