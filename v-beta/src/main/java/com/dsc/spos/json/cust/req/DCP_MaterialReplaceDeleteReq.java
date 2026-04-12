package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MaterialReplaceDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MaterialReplaceDeleteReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired
        private List<Datas> datas;
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
