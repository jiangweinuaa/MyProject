package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_MaterialSetsCalculateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "商品列表")
        List<PluList> pluList;
    }

    @Getter
    @Setter
    public class PluList {
        @JSONFieldRequired(display = "品号")
        private String pluNo;
        @JSONFieldRequired(display = "品号")
        private String pUnit;
        @JSONFieldRequired(display = "生产数量")
        private String pQty;
        @JSONFieldRequired(display = "需求日期")
        private String rDate;
        private String upPluNo;
        private String bomNo;
        private String versionNum;

        private List<UpPluList> upPluList;
    }

    @Getter
    @Setter
    public class UpPluList {
        private String upPluNo;
        private String rDate;
        private String upPUnit;
        private String upPQty;
        private String pQty;
    }


}
