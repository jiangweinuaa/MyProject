package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_BankAccountDeleteReq extends JsonBasicReq {

    private levelRequest request;


    @Getter
    @Setter
    public class levelRequest {
        private List<level1Elm> accountlist;
    }

    @Getter
    @Setter
    public class level1Elm {
        private String accountCode;


    }

}
