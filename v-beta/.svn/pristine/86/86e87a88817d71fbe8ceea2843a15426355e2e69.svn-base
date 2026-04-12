package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArBillProcessReq extends JsonBasicReq {


    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {

        private String corp;
        private String eId;
        private List<NoList> noList;
    }

    @NoArgsConstructor
    @Data
    public class NoList {
        private String reconNo;
        private String bDate;
        private String bizPartnerNo;
    }

}
