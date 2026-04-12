package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PStockInTaskCompleteReq extends JsonBasicReq
{

    private levelRequest request;


    @Data
    public class levelRequest
    {
        private String docType;
        private String ofNo;
        private String pTemplateNo;
        private String warehouse;
        private String materialWarehouseNo;
        private String processPlanNo;
        private String task0No;

        private List<level2> datas;

    }

    @Data
    public class level2
    {
        private String oItem;
        private String pluNo;
        private String batchNo;
        private String prodDate;
        private String price;
        private String distriPrice;
        private String mulQty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String pqty;
        private String amt;
        private String distriAmt;
        private String scrapQty;
        private String taskQty;
        private String punit;
        private String bsNo;
        private String warehouse;
        private String featureNo;
        private String memo;
    }

}
