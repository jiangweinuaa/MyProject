package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_MaterialReplaceQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MaterialReplaceQueryReq.LevelElm request;

    @Data
    public class LevelElm {

        private String status;
        private String replaceType;
        private String organizationNo;
        private String category;
        private String keyTxt;
        private String materialPluNo;
        private String materialUnit;
    }
}
