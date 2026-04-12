package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_StockOutNoticePendingDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_StockOutNoticePendingDetailQueryReq.levelRequest request;


    @Data
    public class levelRequest {

        @JSONFieldRequired
        private String objectType;
        @JSONFieldRequired
        private String[] objectID;
        //@JSONFieldRequired
        private String stockOutOrgNo;
        private String templateNo;
        private String billType;
        private String noticeNo;
        private String keyTxt;

    }

}
