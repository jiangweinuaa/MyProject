package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_StockOutRefundCreateReq  extends JsonBasicReq
{

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String stockOutNo;
        private String stockOutNo_origin;
        private String bDate;
        private String memo;
        private String status;
        private String docType;
        private String oType;
        private String ofNo;
        private String receiptOrg;
        private String loadDocType;
        private String loadDocNo;
        private String deliveryNo;
        private String transferShop;
        private String stockOutID;
        private String bsNo;
        private String warehouse;
        private String transferWarehouse;
        private String pTemplateNo;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private String totCqty;
        private String sourceMenu;
        private List<level1Elm> datas;
        private String deaprtId;
        private String employeeId;

        public String getStockOutNo_origin()
        {
            return stockOutNo_origin;
        }

        public void setStockOutNo_origin(String stockOutNo_origin)
        {
            this.stockOutNo_origin = stockOutNo_origin;
        }

        public String getStockOutNo() {
            return stockOutNo;
        }
        public void setStockOutNo(String stockOutNo) {
            this.stockOutNo = stockOutNo;
        }
        public String getbDate() {
            return bDate;
        }
        public void setbDate(String bDate) {
            this.bDate = bDate;
        }
        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getDocType() {
            return docType;
        }
        public void setDocType(String docType) {
            this.docType = docType;
        }
        public String getoType() {
            return oType;
        }
        public void setoType(String oType) {
            this.oType = oType;
        }
        public String getOfNo() {
            return ofNo;
        }
        public void setOfNo(String ofNo) {
            this.ofNo = ofNo;
        }
        public String getReceiptOrg() {
            return receiptOrg;
        }
        public void setReceiptOrg(String receiptOrg) {
            this.receiptOrg = receiptOrg;
        }
        public String getLoadDocType() {
            return loadDocType;
        }
        public void setLoadDocType(String loadDocType) {
            this.loadDocType = loadDocType;
        }
        public String getLoadDocNo() {
            return loadDocNo;
        }
        public void setLoadDocNo(String loadDocNo) {
            this.loadDocNo = loadDocNo;
        }
        public String getDeliveryNo() {
            return deliveryNo;
        }
        public void setDeliveryNo(String deliveryNo) {
            this.deliveryNo = deliveryNo;
        }
        public String getTransferShop() {
            return transferShop;
        }
        public void setTransferShop(String transferShop) {
            this.transferShop = transferShop;
        }
        public String getStockOutID() {
            return stockOutID;
        }
        public void setStockOutID(String stockOutID) {
            this.stockOutID = stockOutID;
        }
        public String getBsNo() {
            return bsNo;
        }
        public void setBsNo(String bsNo) {
            this.bsNo = bsNo;
        }
        public String getWarehouse() {
            return warehouse;
        }
        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }
        public String getTransferWarehouse() {
            return transferWarehouse;
        }
        public void setTransferWarehouse(String transferWarehouse) {
            this.transferWarehouse = transferWarehouse;
        }
        public String getpTemplateNo() {
            return pTemplateNo;
        }
        public void setpTemplateNo(String pTemplateNo) {
            this.pTemplateNo = pTemplateNo;
        }
        public String getTotPqty() {
            return totPqty;
        }
        public void setTotPqty(String totPqty) {
            this.totPqty = totPqty;
        }
        public String getTotAmt() {
            return totAmt;
        }
        public void setTotAmt(String totAmt) {
            this.totAmt = totAmt;
        }
        public String getTotDistriAmt() {
            return totDistriAmt;
        }
        public void setTotDistriAmt(String totDistriAmt) {
            this.totDistriAmt = totDistriAmt;
        }
        public String getTotCqty() {
            return totCqty;
        }
        public void setTotCqty(String totCqty) {
            this.totCqty = totCqty;
        }
        public List<level1Elm> getDatas() {
            return datas;
        }
        public void setDatas(List<level1Elm> datas) {
            this.datas = datas;
        }
        public String getSourceMenu() {
            return sourceMenu;
        }
        public void setSourceMenu(String sourceMenu) {
            this.sourceMenu = sourceMenu;
        }

        public String getDeaprtId() {
            return deaprtId;
        }

        public void setDeaprtId(String deaprtId) {
            this.deaprtId = deaprtId;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }
    }

    public class level1Elm
    {
        private String item;
        private String oItem;
        private String pluNo;
        private String punit;
        private String pqty;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String price;
        private String amt;
        private String distriAmt;
        private String pluBarcode;
        private String warehouse;
        private String bsNo;
        private String pluMemo;
        private String batchNo;
        private String prodDate;
        private String distriPrice;
        private String punitUdLength;
        private String featureNo;
        /**
         * 基准单位库存量
         */
        private String Stockqty;
        private String item_origin;
        private String pqty_origin;

        private String location;
        private String expDate;


        public String getItem_origin()
        {
            return item_origin;
        }

        public void setItem_origin(String item_origin)
        {
            this.item_origin = item_origin;
        }

        public String getPqty_origin()
        {
            return pqty_origin;
        }

        public void setPqty_origin(String pqty_origin)
        {
            this.pqty_origin = pqty_origin;
        }

        public String getStockqty()
        {
            return Stockqty;
        }

        public void setStockqty(String stockqty)
        {
            Stockqty = stockqty;
        }

        public String getItem() {
            return item;
        }
        public void setItem(String item) {
            this.item = item;
        }
        public String getoItem() {
            return oItem;
        }
        public void setoItem(String oItem) {
            this.oItem = oItem;
        }
        public String getPluNo() {
            return pluNo;
        }
        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }
        public String getPunit() {
            return punit;
        }
        public void setPunit(String punit) {
            this.punit = punit;
        }
        public String getPqty() {
            return pqty;
        }
        public void setPqty(String pqty) {
            this.pqty = pqty;
        }
        public String getBaseUnit() {
            return baseUnit;
        }
        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }
        public String getBaseQty() {
            return baseQty;
        }
        public void setBaseQty(String baseQty) {
            this.baseQty = baseQty;
        }
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getAmt() {
            return amt;
        }
        public void setAmt(String amt) {
            this.amt = amt;
        }
        public String getDistriAmt() {
            return distriAmt;
        }
        public void setDistriAmt(String distriAmt) {
            this.distriAmt = distriAmt;
        }
        public String getPluBarcode() {
            return pluBarcode;
        }
        public void setPluBarcode(String pluBarcode) {
            this.pluBarcode = pluBarcode;
        }
        public String getWarehouse() {
            return warehouse;
        }
        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }
        public String getBsNo() {
            return bsNo;
        }
        public void setBsNo(String bsNo) {
            this.bsNo = bsNo;
        }
        public String getPluMemo() {
            return pluMemo;
        }
        public void setPluMemo(String pluMemo) {
            this.pluMemo = pluMemo;
        }
        public String getBatchNo() {
            return batchNo;
        }
        public void setBatchNo(String batchNo) {
            this.batchNo = batchNo;
        }
        public String getProdDate() {
            return prodDate;
        }
        public void setProdDate(String prodDate) {
            this.prodDate = prodDate;
        }
        public String getDistriPrice() {
            return distriPrice;
        }
        public void setDistriPrice(String distriPrice) {
            this.distriPrice = distriPrice;
        }
        public String getPunitUdLength() {
            return punitUdLength;
        }
        public void setPunitUdLength(String punitUdLength) {
            this.punitUdLength = punitUdLength;
        }
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }
    }


}
