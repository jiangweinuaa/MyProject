package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_InterSettleDataQueryRes extends JsonRes {


    private Datas datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private List<Process> process;
    }

    @NoArgsConstructor
    @Data
    public class Process {
        private Integer totCqty;
        private double totPqty;
        private double totReceiveAmt;
        private double totSupplyAmt;
        private List<Route> route;
        private List<PluDetail> pluDetail;
    }

    @NoArgsConstructor
    @Data
    public class Route {
        private String processNo;
        private String versionNum;
        private String pSortId;
        private String station;
        private String stationName;
        private String rowNum;
        private String bType;
        private String direction;
    }

    @NoArgsConstructor
    @Data
    public class PluDetail {
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String pUnit;
        private String pUnitName;
        private Double pQty;
        private Double receivePrice;
        private Double receiveAmt;
        private Double supplyPrice;
        private Double supplyAmt;
        private String taxCode;
        private String taxName;
        private Double taxRate;
        private String receiveOrgNo;
        private String receiveOrgName;
        private String receiveCorp;
        private String receiveCorpName;
        private String supplyOrgNo;
        private String supplyOrgName;
        private String supplyCorp;
        private String supplyCorpName;
        private String processNo;
        private String versionNum;
        private String direction;
        private String bType;
        private String settleCorp;
        private String settleCorpName;
        private Double preTaxAmt;
        private Double taxAmt;
        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;
        private String outputTaxCode;
        private String outputTaxRate;
    }
}

