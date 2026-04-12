package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 预约单状态变更
 * @author: wangzyc
 * @create: 2021-08-03
 */
@Data
public class DCP_ReserveOrderStatusUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm {
        private String shopId; // 所属门店
        private String operatorId; // 操作人编号，对应表.修改人
        private String operatorName; // 操作人名称
        private String reserveNo; // 预约单号
        private String loadDocType; // 来源渠道类型
        private String status; // 单据状态 0待审核 1待服务 2已服务 3已取消
        private String opNo; // 顾问编号
    }
}
