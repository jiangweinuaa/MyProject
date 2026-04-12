package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约项目新增
 * @author: wangzyc
 * @create: 2021-07-21
 */
@Data
public class DCP_ReserveItemsCreateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String itemsNo; // 项目编号
        private String shopId; // 所属门店
        private List<level2Elm> opList; // 分配顾问
        private String status;  // 状态
    }

    @Data
    public class level2Elm{
        private String opNo;
    }
}
