package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import org.json.JSONObject;

public class DCP_ShopManagePrintCreateReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String eId;
        private String shopId;
        private String printBillType;
        private String billNo;
        private String datas;

        public String geteId() {
            return eId;
        }

        public void seteId(String eId) {
            this.eId = eId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getPrintBillType() {
            return printBillType;
        }

        public void setPrintBillType(String printBillType) {
            this.printBillType = printBillType;
        }

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getDatas() {
            return datas;
        }

        public void setDatas(String datas) {
            this.datas = datas;
        }
    }
}
