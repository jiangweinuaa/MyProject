package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约时间修改
 * @author: wangzyc
 * @create: 2021-07-20
 */
@Data
public class DCP_ReserveTimeUpdateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId;  // 所属门店
        private level2Elm reserveTime;
    }

    @Data
    public class level2Elm {
        private String item;     // 项次
        private String cycle;    // 周期[1,2,3,4,5,6,7]
        private String beginTime;   // 开始时间
        private String endTime;   // 结束时间
        private String status;    //  状态
    }
}
