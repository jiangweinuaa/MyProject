package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReturnApplyStatusUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ReturnApplyStatusUpdateReq.level1Elm request;

    @Data
    public class level1Elm{
        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String opType;

        private List<GoodList> goodsList;
    }

    @Data
    public class GoodList{
        private String item;
        private String pluNo;
        private String featureNo;
        private String batchNo;
        private String approvePrice;
        private String approveQty;
        //private String approveEmpId;
        //private String approveDeptId;
        //private String approveDate;
        private String reason;
    }
}
