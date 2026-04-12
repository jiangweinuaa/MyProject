package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProdScheduleCreateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProdScheduleCreateReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String bDate;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        @JSONFieldRequired
        private String semiWOGenType;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;

        private String memo;
        @JSONFieldRequired
        private String totCqty;
        @JSONFieldRequired
        private String totPqty;
        @JSONFieldRequired
        private List<Detail> detail;
    }

    @Data
    public class Detail{
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        @JSONFieldRequired
        private String rDate;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String pQty;
        @JSONFieldRequired
        private String poQty;
        @JSONFieldRequired
        private String pGroupNo;
        @JSONFieldRequired
        private String stockQty;
        @JSONFieldRequired
        private String shortQty;
        @JSONFieldRequired
        private String adviceQty;
        @JSONFieldRequired
        private String minQty;
        @JSONFieldRequired
        private String mulQty;
        @JSONFieldRequired
        private String remainType;
        @JSONFieldRequired
        private String preDays;
        @JSONFieldRequired
        private String baseUnit;
        @JSONFieldRequired
        private String unitRatio;
        @JSONFieldRequired
        private String bomNo;
        @JSONFieldRequired
        private String versionNum;
        @JSONFieldRequired
        private String sourceType;
        private String memo;
        private String oddValue;

        @JSONFieldRequired
        private List<SourceList> sourceList;
    }

    @Data
    public class SourceList {
        @JSONFieldRequired
        public String item;
        @JSONFieldRequired
        public String orderType;
        @JSONFieldRequired
        public String orderNo;
        @JSONFieldRequired
        public String orderItem;
        @JSONFieldRequired
        public String objectType;
        @JSONFieldRequired
        public String objectId;
        @JSONFieldRequired
        public String rUnit;
        @JSONFieldRequired
        public String rQty;
        //@JSONFieldRequired
        public String pUnit;
        @JSONFieldRequired
        public String poQty;
        //@JSONFieldRequired
        public String ptemplateNo;

    }
}
