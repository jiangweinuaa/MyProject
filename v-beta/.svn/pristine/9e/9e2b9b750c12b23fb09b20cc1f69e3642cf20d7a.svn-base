package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_AccCOACreateReq extends JsonBasicReq {

    private DCP_AccCOACreateReq.levelRequest request;

    @Data
    public class levelRequest {
        private String coaRefID;
        private String subjectId;
        //private String accountId;
        private List<AccList> accList;
    }

    @Data
    public class AccList{
        private String accountId;
    }


}
