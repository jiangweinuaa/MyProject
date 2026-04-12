package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_BizpartnerOpenQryReq extends JsonBasicReq {
    private DCP_BizpartnerOpenQryReq.levelElm request;

    @Data
    public class levelElm{
        private String keyTxt;
        private String status;
        private String ischeck_restrictgroup;
        private String groupType;
        private String[] bizType;
       }
}
