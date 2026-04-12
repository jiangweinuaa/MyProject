package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArWrtOffDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        private String accountId;
        @JSONFieldRequired
        private String arNo;
        private List<WrtOffList> wrtOffList;
    }

    @NoArgsConstructor
    @Data
    public class WrtOffList {
        private List<ArRecList> arRecList;
        private List<ArWFList> arWFList;
    }

    @NoArgsConstructor
    @Data
    public class ArRecList {
        private String item;
    }

    @NoArgsConstructor
    @Data
    public class ArWFList {
        private String item;
    }
}


