package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_WOGenerateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_WOGenerateReq.level1Elm request;

    @Data
    public class level1Elm
    {
        @JSONFieldRequired
        private List<Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String pGroupNo;
        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String prodType;
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pluNo;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        @JSONFieldRequired
        private String bomNo;
        @JSONFieldRequired
        private String versionNum;
        @JSONFieldRequired
        private String featureNo;
        //@JSONFieldRequired
        private String minQty;
        //@JSONFieldRequired
        private String mulQty;

        //加上基础数量  服务自己算
        private String baseUnit;
        private String baseQty;
        private String unitRatio;

    }

}
