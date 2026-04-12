package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TransApplyStatusUpdateReq extends JsonBasicReq {

    private DCP_TransApplyStatusUpdateReq.LevelElm request;

    @Data
    public class LevelElm{

        @JSONFieldRequired(display = "申请单号")
        private String billNo;
        @JSONFieldRequired(display = "操作类型")
        private String opType;
        private String reason;
        private List<GoodsList> goodsList;

    }

    @Data
    public class GoodsList{

        @JSONFieldRequired(display = "项次")
        private String item;
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired(display = "核准数量")
        private String pQty;
        private String reason;
    }
}