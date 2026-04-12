package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_SettleDataUpdateReq extends JsonBasicReq {


    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String organizationNo;
        private String org_Name;
        private String bType;
        private String billNo;
        private String bizType;
        private String bizPartnerNo;

        private String createBy;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirm_Date;
        private String confirm_Time;
        private String accountBy;
        private String account_Date;
        private String account_Time;
        private String cancelBy;
        private String cancel_Date;
        private String cancel_Time;

        private List<AdjList> adjList;
    }

    @NoArgsConstructor
    @Data
    public class AdjList {
        private String item;
        private String billNo;
        private String status;
        private String unSettleAmt;
        private String settleAmt;
        private String unPaidAmt;
        private String paidAmt;
        private String apQty;
        private String apAmt;
        private String unApAmt;
    }
}

