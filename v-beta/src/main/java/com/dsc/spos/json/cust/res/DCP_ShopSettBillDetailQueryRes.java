package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ShopSettBillDetailQueryRes extends JsonRes {


    private List<Datas> datas;

    @NoArgsConstructor
    @Data
    public class Datas {
        private String status;
        private String bDate;
        private String reconNo;
        private String shopId;
        private String sourceType;
        private String bizPartnerNo;
        private String receiver;
        private String payDateNo;
        private String currency;
        private String exRate;
        private String taxCode;
        private String taxRate;
        private String tot_Amt;
        private String payAmt;
        private String arNo;
        private String arNo2;
        private List<ShopSettList> shopSettList;
        private List<ShopRecList> shopRecList;
    }

    @NoArgsConstructor
    @Data
    public class ShopSettList {

        private String shopId;
        private String corp;
        private String reconNo;
        private String item;
        private String sourceNo;
        private String sourceItem;
        private String pluBarcode;
        private String pluNo;
        private String featureNo;
        private String taxCode;
        private String oldPrice;
        private String price;
        private String qty;
        private String sUnit;
        private String discAmt;
        private String amt;
        private String btAmt;
        private String lCYTATAmt;
        private String isGift;
        private String cardNo;
        private String sendPay;
        private String shopQty;
    }

    @NoArgsConstructor
    @Data
    public  class ShopRecList {

        private String shopId;
        private String corp;
        private String reconNo;
        private String item;
        private String accItem;
        private String payType;
        private String payCode;
        private String payCodeNo;
        private String amt;
        private String cardNo;
        private String couponQty;
        private String ischanged;
        private String extra;
        private String descore;
        private String refundType;
        private String accDate;
        private String isWrtOff;
        private String isAcc;
        private String isOrderPay;
        private String totChanged;
        private String sendPay;
        private String orderNo;
        private String sellerDisc;
        private String thirdDiscount;
        private String couponMarketPrice;
        private String couponPrice;
        private String bDate;
        private String eraseAmt;
        private String squadNo;
        private String machineNo;
        private String opNo;
    }
}

