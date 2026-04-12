package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MoCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MoCreateReq.level1Elm request;

    @Data
    public class level1Elm{
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String pDate;
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private String pGroupNo;
        @JSONFieldRequired
        private String departId;
        @JSONFieldRequired
        private String prodType;
        private String ofNo;
        private String oType;
        private String sourceMoNo;
        private String loadDocNo;
        private String memo;
        private List<Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String item;
        private String oItem;
        private String sourceMoItem;
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
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
        private String pickStatus;
        private String dispatchQty;
        private String dispatchStatus;
        @JSONFieldRequired
        private String mulQty;
        @JSONFieldRequired
        private String minQty;
    }
}
