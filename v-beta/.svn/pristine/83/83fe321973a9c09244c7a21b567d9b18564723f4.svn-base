package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_PrintTemplateUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PrintTemplateUpdateReq.Request request;

    @Getter
    @Setter
    public class Request {

        @JSONFieldRequired
        private String modularNo;
        @JSONFieldRequired
        private List<DCP_PrintTemplateUpdateReq.DetailInfo> detail;
    }

    @Data
    public class DetailInfo{
        @JSONFieldRequired
        private String printNo;
        @JSONFieldRequired
        private String printName;
        @JSONFieldRequired
        private String proName;
        @JSONFieldRequired
        private String parameter;
        @JSONFieldRequired
        private String isStandard;
        @JSONFieldRequired
        private String isDefault;
        @JSONFieldRequired
        private String printType;
        private int restrictOp;
        private List<DCP_PrintTemplateUpdateReq.restrictOpList> restrictOpList;
        private String restrictOrg;
        private List<DCP_PrintTemplateUpdateReq.restrictOrgList> restrictOrgList;
        private String restrictCust;
        private List<DCP_PrintTemplateUpdateReq.restrictCustList> restrictCustList;
        @JSONFieldRequired
        private String status;
    }

    @Data
    public class restrictOpList{
        private String opType;
        private String id;
    }

    @Data
    public class restrictOrgList{
        private String orgNo;
    }

    @Data
    public class restrictCustList{
        private String customerNo;
    }
}
