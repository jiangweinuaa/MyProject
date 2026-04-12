package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_TransApplyDetailQueryReq extends JsonBasicReq {

    private DCP_TransApplyDetailQueryReq.LevelElm request;

    @Data
    public class LevelElm{

        @JSONFieldRequired(display = "申请单号")
        private String billNo;
        private String getType;

    }
}