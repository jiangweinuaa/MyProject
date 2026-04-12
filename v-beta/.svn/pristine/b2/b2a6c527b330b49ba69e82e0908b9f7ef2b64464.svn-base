package com.dsc.spos.json.cust.req;

import java.math.BigDecimal;
import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：SStockOutCreateDCP
 *    說明：自采出库单新增
 * 服务说明：自采出库单新增
 * @author JZMA
 * @since  2018-11-20
 */
public class DCP_SStockOutCreateReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    @Data
    public class levelElm{
        private String bDate;
        private String memo;
        private String status;
        private String supplier;
        private String sStockOutID;
        private String warehouse;
        private String ofNo;
        private String taxCode;//税别编码
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String employeeId;
        private String departId;
        private String orderOrgNo;
        private String payType;
        private String payOrgNo;
        private String billDateNo;
        private String payDateNo;
        private String invoiceCode;
        private String currency;
        private String stockOutType;
        private String customer;
        private String oType;
        private String accountDate;
        private String originNo;
        private String payee;
        private String payer;
        private String bizOrgNo;
        private String bizOrgCorp;
        private String corp;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;
        private String outputTaxCode;
        private String outputTaxRate;

        private List<level1Elm> datas;
        private List<level2Elm> imageList;

    }

    @Data
    public class level1Elm {
        private String item;
        private String oItem;
        private String pluNo;
        private String punit;
        private String pqty;
        private String price;
        private String amt;
        private String warehouse;
        private String batchNo;
        private String prodDate;
        private String distriPrice;
        private String distriAmt;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String featureNo;
        private String oType;
        private String ofNo;
        private String category;
        private String isGift;
        private String location;
        private String expDate;
        private String pTemplateNo;
        private String originNo;
        private String originItem;
        private String oofNo;
        private String ooItem;

        private String taxCode;
        private String taxRate;
        private String inclTax;
        private String taxCalType;

        private String taxAmt;
        private String preTaxAmt;
        private String preDisTaxAmt;
        private String disTaxAmt;
        private String bsNo;
        private String purPrice;
        private String purAmt;
        private String refPurPrice;
        private String custPrice;
        private String custAmt;
        private String refCustPrice;


    }
    public class level2Elm{
        private String image;

        public String getImage() {
            return image;
        }
        public void setImage(String image) {
            this.image = image;
        }
    }

}

