package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_MaterialReplaceCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MaterialReplaceCreateReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired
        private String replaceType;
        private String organizationNo;
        @JSONFieldRequired
        private String materialPluNo;
        @JSONFieldRequired
        private String materialQty;
        @JSONFieldRequired
        private String materialUnit;
        @JSONFieldRequired
        private String replacePluNo;
        @JSONFieldRequired
        private String replaceQty;
        @JSONFieldRequired
        private String replaceUnit;
        @JSONFieldRequired
        private String replaceBDate;
        @JSONFieldRequired
        private String replaceEDate;
        @JSONFieldRequired
        private String status;
        private String priority;
        private String memo;
    }
}
