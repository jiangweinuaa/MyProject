package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_ReceivingAddReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
        private String eId;//企业id
        private String organizationNo;//
        private String supplierNo;//
        private String docNo;//
        private String totQty;//
        private String totCQty;//
        private String opNo;//
        private String opName;//
        private String status;//
        private String department;//
        private List<level1> datas;
    }


    @Data
    public class level1
    {
        private String oItem;//
        private String pluNo;//
        private String featureNo;//
        private String unit;//
        private String qty;//
        private String baseUnit;//
        private String baseQty;//
        private String unitRatio;//
        private String distriPrice;//
        private String distriAmount	;//
        private String warehouseNo;//
        private String rDate;//
        private String isGive;//

    }


}
