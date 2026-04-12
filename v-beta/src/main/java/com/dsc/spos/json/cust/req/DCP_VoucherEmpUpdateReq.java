package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_VoucherEmpUpdateReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {

        private  String opNo;
        private  String opName;
        private  String opNoOut;

    }

}
