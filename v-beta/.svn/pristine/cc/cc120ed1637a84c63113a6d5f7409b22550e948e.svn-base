package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_SupplierGoodsOpenQryReq extends JsonBasicReq {

    private DCP_SupplierGoodsOpenQryReq.levelItem request;

    @Data
    public class levelItem {
        private String supplier;
        private String[] category;
        private String[] pluBarcode;
        private String[] pluNo;
        private String[] purType;
        private String orgNo;
        private String queryStockqty;
        private String purTemplateNo;
        private String keyTxt;
    }

}
