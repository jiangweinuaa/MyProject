package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.JsonReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PendingPOrderQueryReq extends JsonBasicReq {


    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        private String dateType;
        private String beginDate;
        private String endDate;
        private List<String> orgList;
        private String keyTxt;
        private String getType;
    }
}
