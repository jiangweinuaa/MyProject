package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 开票记录查询
 * @author: wangzyc
 * @create: 2022-03-15
 */
@Data
public class DCP_EInvoiceQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String invoiceBillNo;
        private String sourBillType;
        private String platformType;
        private String platformName;
        private String invoiceType;
        private String invoiceKind;
        private String amt;
        private String taxAmt;
        private String exTaxAmt;
        private String drawer;
        private String receiver;
        private String reviewer;
        private String saleTaxNum;
        private String saleTel;
        private String saleAddress;
        private String saleBank;
        private String saleAccount;
        private String buyerName;
        private String buyerTaxNum;
        private String buyerTel;
        private String buyerAddress;
        private String buyerBank;
        private String buyerAccount;
        private String buyerPhone;
        private String email;
        private String isApply;
        private String applyDate;
        private String applyStatusMsg_platforml;
        private String status;
        private String statusMsg_platform;
        private String invoiceDate;
        private String invoiceSerialNum;
        private String invoiceCode;
        private String invoiceNo;
        private String pdfUrl;
        private String pictureUrl;
        private String isManual;
        private String opNo;
        private String opName;
        private List<level2Elm> businessList;

        private List<level3Elm> detailList;
    }

    @Data
    public class level2Elm{
        private String sourceShopId;
        private String sourceShopName;
        private String sourceBillNo;
    }

    @Data
    public class level3Elm{
        private String item;
        private String goodsName;
        private String num;
        private String price;
        private String spbm;
        private String taxRate;
        private String amt;
        private String taxAmt;
        private String exTaxAmt;
    }

}
