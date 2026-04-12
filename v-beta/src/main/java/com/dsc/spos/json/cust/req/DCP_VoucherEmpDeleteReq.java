package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_VoucherEmpDeleteReq extends JsonBasicReq
{


    private levelRequest request;

    @Data
    public class levelRequest
    {
        private List<level1Elm> empList;

    }

    @Data
    public class level1Elm
    {
        private  String opNo;

    }



}
