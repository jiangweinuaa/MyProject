package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PayClassSubjectCreateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
        private String status;
        @JSONFieldRequired
        private List<ClassList> classList;
    }

    @NoArgsConstructor
    @Data
    public class ClassList {
        @JSONFieldRequired
        private String classNo;
        private String debitSubject;
        private String paySubject;
        private String revSubject;
        private String advSubject;
    }

}
