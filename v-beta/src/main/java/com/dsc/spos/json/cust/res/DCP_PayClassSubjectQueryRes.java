package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PayClassSubjectQueryRes extends JsonRes {


    private List<Request> request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String accountId;
        private String account;
        private String coaRefID;
        private String status;
    }
}
