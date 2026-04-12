package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

public class DCP_StockInRefundUpdateReq extends JsonBasicReq
{

    private levelElm request;
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    @Data
    public class levelElm{
        private String stockInNo;
        private String bDate;
        private String memo;
        private String status;
        private String docType;
        private String oType;
        private String ofNo;
        private String pTemplateNo;
        private String loadDocType;
        private String loadDocNo;
        private String stockInID;
        private String bsNo;
        private String warehouse;
        private String transferShop;
        private String receiptDate;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String packingNo;
        private String stockInNo_origin;
        private String employeeId;
        private String departId;

        private String corp;
        private String deliveryCorp;

        private List<level1Elm> datas;



    }

    @Data
    public class level1Elm
    {
        private String item;
        private String oType;
        private String ofNo;
        private String oItem;
        private String ooType;
        private String oofNo;
        private String ooItem;
        private String pluNo;
        private String punit;
        private String pqty;
        private String poQty;
        private String receivingQty;
        private String price;
        private String amt;
        private String pluBarcode;
        private String warehouse;
        private String pluMemo;
        private String batchNo;
        private String prodDate;
        private String distriPrice;
        private String distriAmt;
        private String punitUdLength;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String featureNo;
        private String packingNo;
        private String item_origin;
        private String pqty_origin;
        private String pqty_refund;

        private String location;
        private String expDate;

    }


}
