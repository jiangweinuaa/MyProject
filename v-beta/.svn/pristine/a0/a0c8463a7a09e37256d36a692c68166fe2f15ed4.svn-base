package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ApWrtOffDeleteReq extends JsonBasicReq {


    private Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        private String status;
        private String accountId;
        private String wrtOffNo;
        private List<WrtOffList> wrtOffList;



    }

    @NoArgsConstructor
    @Data
    public static class WrtOffList {
        private List<PmtList> pmtList;
        private List<ApWFList> apWFList;


    }

    @NoArgsConstructor
    @Data
    public static class PmtList {
        private String item;
    }

    @NoArgsConstructor
    @Data
    public static class ApWFList {
        private String item;
    }
}
