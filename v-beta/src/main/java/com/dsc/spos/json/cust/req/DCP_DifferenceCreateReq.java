package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DCP_DifferenceCreateReq extends JsonBasicReq {
    @JSONFieldRequired
    private levelElm request;

    @Setter
    @Getter
    public class levelElm {
        @JSONFieldRequired
        private String bDate;
        private String memo;
        private String status;
        @JSONFieldRequired
        private String docType;
        @JSONFieldRequired
        private String ofNo;
        @JSONFieldRequired
        private String oType;
        @JSONFieldRequired
        private String loadDocType;
        @JSONFieldRequired
        private String loadDocNo;
        private String differenceID;
        @JSONFieldRequired
        private String transferOutShop;
        @JSONFieldRequired
        private String transferInShop;
        @JSONFieldRequired
        private String warehouse;
        @JSONFieldRequired
        private String invWarehouse;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        @JSONFieldRequired
        private String transferWarehouse;
        @JSONFieldRequired
        private String createType;
        @JSONFieldRequired
        private String transferShop;
        private String transferShopName;

        private List<level1Elm> datas;

    }
    @Setter
    @Getter
    public class level1Elm {
        private String item;
        private String oType;
        private String ofNo;
        private String oItem;
        private String ooType;
        private String oofNo;
        private String ooItem;
        private String pluNo;
        private String pluName;
        private String spec;
        private String punit;
        private String punitName;
        private String receivingQty;
        private String realQty;
        private String poQty;
        private String diffReqQty;
        private String diffQty;
        private String reqQty;
        private String price;
        private String amt;

        private String warehouse;
        private String pluBarcode;
        private String pluMemo;
        private String batchNo;
        private String isBatch;
        private String prodDate;
        private String distriPrice;
        private String distriAmt;
        private String load_item;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String featureNo;
        private String bsNo;
        private String bsName;
        private String transferBatchNo;


    }


}
