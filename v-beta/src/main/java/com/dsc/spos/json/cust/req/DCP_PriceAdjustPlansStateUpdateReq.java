package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_PriceAdjustPlansStateUpdateReq extends JsonBasicReq {

    @Getter
    @Setter
    private Request request;

    @Getter
    @Setter
    public class Request{

        private String oprType;  //枚举: execute：执行任务,terminate：终止任务

        private List<Bill> billList;

    }

    @Getter
    @Setter
    public class Bill{
        private String billNo;
        private String item;
    }

}
