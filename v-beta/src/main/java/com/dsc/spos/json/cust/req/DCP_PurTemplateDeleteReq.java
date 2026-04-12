package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurTemplateDeleteReq extends JsonBasicReq {

    private DCP_PurTemplateDeleteReq.level1Elm request;

    @Data
    public class level1Elm{

        private String purTemplateNo;
    }

}
