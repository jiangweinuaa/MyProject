package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DCP_BatchingTaskMaterialReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_BatchingTaskMaterialReq.Request request;

    @Getter
    @Setter
    public class Request {
        private String batchTaskNo;
        private String pluNo;
        private String pQty;
        private String pUnit;
        private String processNo;
        private String materialPluNo;
    }
}
