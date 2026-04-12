package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;


@Data
public class DCP_DistriOrderQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private LevelElm request;

    @Data
    public class LevelElm{
        private String status;
        private String beginDate;
        private String endDate;
        private String keytxt;
    }

}
