package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PTemplateCalculateReq extends JsonBasicReq {

    private DCP_PTemplateCalculateReq.levelElm request;

    @Data
    public class levelElm{
        //@JSONFieldRequired(display = "需求日期")
        //private String rDate;
        @JSONFieldRequired(display = "发货组织编号")
        private String receiptOrgNo;
        private String isTemplateControl;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;

    }
}
