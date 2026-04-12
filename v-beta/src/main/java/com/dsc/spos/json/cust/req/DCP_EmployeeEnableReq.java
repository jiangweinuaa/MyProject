package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_EmployeeEnableReq extends JsonBasicReq {

    private Request request;

    @Setter
    @Getter
    public class Request{

        private String oprType;
        private List<EmployeeNo> enablelist;

    }

    @Getter
    @Setter
    public class EmployeeNo{
        private String employeeNo;
    }


}
