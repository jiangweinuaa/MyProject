package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * 影响因素修改
 *
 * @author yuanyy 20191021
 */
@Setter
@Getter
public class DCP_CustomerPOrderStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private levelElm request;

    @Setter
    @Getter
    public class levelElm {
        private String eId;
        private String shopId;
        @JSONFieldRequired
        private String pOrderNo;
        private String status;
        @JSONFieldRequired
        private String opType;

    }


}
