package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MaterialReplaceEnableReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MaterialReplaceEnableReq.LevelElm request;

    @Data
    public class LevelElm{

        @JSONFieldRequired
        private String opType;

        @JSONFieldRequired
        private List<DCP_MaterialReplaceEnableReq.Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String replaceType;
        private String organizationNo;
        @JSONFieldRequired
        private String materialPluNo;
        @JSONFieldRequired
        private String materialUnit;
        @JSONFieldRequired
        private String replacePluNo;
    }

}
