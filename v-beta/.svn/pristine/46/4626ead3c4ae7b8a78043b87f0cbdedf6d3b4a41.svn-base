package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProdTemplateUpdateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProdTemplateUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String templateId;
        @JSONFieldRequired
        private String templateName;
        @JSONFieldRequired
        private String restrictOrg;
        private String memo;
        @JSONFieldRequired
        private String status;

        @JSONFieldRequired
        public List<DCP_ProdTemplateUpdateReq.Detail> detail;

        @JSONFieldRequired
        public List<DCP_ProdTemplateUpdateReq.OrgList> orgList;

    }

    @Data
    public class Detail{
        @JSONFieldRequired
        private String pluNo;
        @JSONFieldRequired
        private String prodUnit;
        @JSONFieldRequired
        private String prodMinQty;
        @JSONFieldRequired
        private String prodMulQty;
        @JSONFieldRequired
        private String remainType;
        @JSONFieldRequired
        private String dispType;
        @JSONFieldRequired
        private String procRate;
        @JSONFieldRequired
        private String semiWoType;
        @JSONFieldRequired
        private String semiWoDeptType;
        @JSONFieldRequired
        private String sdLaborTime;
        @JSONFieldRequired
        private String sdMachineTime;
        @JSONFieldRequired
        private String fixPreDays;
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private String oddValue;
        @JSONFieldRequired
        private String productExceed;
        @JSONFieldRequired
        private String standardHours;
    }

    @Data
    public class OrgList{
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String status;
    }

}
