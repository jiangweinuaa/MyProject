package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReturnApplyUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ReturnApplyUpdateReq.level1Elm request;

    @Data
    public class level1Elm {

        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String bdate;
        @JSONFieldRequired
        private String employeeId;
        @JSONFieldRequired
        private String departId;
        private String memo;
        private List<DCP_ReturnApplyUpdateReq.Detail> detail;
    }

    @Data
    public class Detail{
        @JSONFieldRequired
        private String item;
        @JSONFieldRequired
        private String pluBarcode;
        @JSONFieldRequired
        private String pluNo;
        private String featureNo;
        private String supplierType;
        private String supplierId;
        @JSONFieldRequired
        private String pQty;
        private String unitRatio;
        @JSONFieldRequired
        private String pUnit;
        private String batchNo;
        private String prodDate;
        @JSONFieldRequired
        private String expDate;
        private String bsNo;
        @JSONFieldRequired
        private String price;
        @JSONFieldRequired
        private String Amt;
        @JSONFieldRequired
        private String distriPrice;
        @JSONFieldRequired
        private String distriAmt;
        private String memo;
        private String ofNo;
        private String oItem;
        @JSONFieldRequired
        private List<DCP_ReturnApplyUpdateReq.ImageList> imageList;
    }

    @Data
    public class ImageList{
        @JSONFieldRequired
        private String image;
    }

}
