package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约时间查询
 * @author: wangzyc
 * @create: 2021-08-02
 */
@Data
public class DCP_ReserveTimeQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 所属门店
        private String opNo; // 员工编号，模式1必传
        private String itemsNo; // 项目编号
        private String date; // 所选日期
    }

}
