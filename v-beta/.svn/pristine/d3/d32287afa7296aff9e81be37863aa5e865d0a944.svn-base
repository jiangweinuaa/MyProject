package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ApWrtOffStatusUpdateReq extends JsonBasicReq {


    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        private List<WrtOffListLevel> wrtOffList;
        private String status;
        private String opType;
        private String eId;
        private String accountId;


    }

    @NoArgsConstructor
    @Data
    public  class WrtOffListLevel {
        private String wrtOffNo;
    }
}
