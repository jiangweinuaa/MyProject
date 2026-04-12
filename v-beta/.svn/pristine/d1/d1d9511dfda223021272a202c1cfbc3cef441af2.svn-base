package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 门店休息日新增
 * @author: wangzyc
 * @create: 2021-07-27
 */
@Data
public class DCP_HolidayCreateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;
        private String beginDate;
        private String endDate;
        private String memo;
    }
}
