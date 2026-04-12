package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：LStockOutCreate
 *    說明：报损单新增
 * 服务说明：报损单新增
 * @author luoln
 * @since  2017-03-27
 */
public class DCP_LStockOutCreateReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String bDate;
        private String memo;
        private String lStockOutID;
        private String warehouse;
        private String pTemplateNo;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private String totCqty;
        private String kdsLStockOut; // 非必传，KDS报损Y/N--新增
        private String shopId;
        private String feeObjectType;
        private String feeObjectId;
        private String fee;
        private String bFeeNo;
        private String employeeId;
        private String departId;

        private List<level1Elm> datas;
        private List<level1ElmFileList>fileList;

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
        public String getlStockOutID() {
            return lStockOutID;
        }
        public void setlStockOutID(String lStockOutID) {
            this.lStockOutID = lStockOutID;
        }
        public String getWarehouse() {
            return warehouse;
        }
        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
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
        public List<level1ElmFileList> getFileList() {
            return fileList;
        }
        public void setFileList(List<level1ElmFileList> fileList) {
            this.fileList = fileList;
        }
        public String getKdsLStockOut() {
            return kdsLStockOut;
        }
        public void setKdsLStockOut(String kdsLStockOut) {
            this.kdsLStockOut = kdsLStockOut;
        }
        public String getShopId() {
            return shopId;
        }
        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getFeeObjectType() {
            return feeObjectType;
        }

        public void setFeeObjectType(String feeObjectType) {
            this.feeObjectType = feeObjectType;
        }

        public String getFeeObjectId() {
            return feeObjectId;
        }

        public void setFeeObjectId(String feeObjectId) {
            this.feeObjectId = feeObjectId;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getDepartId() {
            return departId;
        }

        public void setDepartId(String departId) {
            this.departId = departId;
        }

        public String getbFeeNo() {
            return bFeeNo;
        }

        public void setbFeeNo(String bFeeNo) {
            this.bFeeNo = bFeeNo;
        }
    }

    public class level1Elm {
        private String item;
        private String pluNo;
        private String punit;
        private String pqty;
        private String price;
        private String bsNo;
        private String amt;
        private String warehouse;
        private String batchNo;
        private String prodDate;
        private String distriPrice;
        private String distriAmt;
        private String baseUnit;
        private String unitRatio;
        private String baseQty;
        private String featureNo;
        private String pluBarCode; // 非必传，商品条码--新增

        private String location;
        private String expDate;

        public String getItem() {
            return item;
        }
        public void setItem(String item) {
            this.item = item;
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
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getBsNo() {
            return bsNo;
        }
        public void setBsNo(String bsNo) {
            this.bsNo = bsNo;
        }
        public String getAmt() {
            return amt;
        }
        public void setAmt(String amt) {
            this.amt = amt;
        }
        public String getWarehouse() {
            return warehouse;
        }
        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
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
        public String getDistriAmt() {
            return distriAmt;
        }
        public void setDistriAmt(String distriAmt) {
            this.distriAmt = distriAmt;
        }
        public String getBaseUnit() {
            return baseUnit;
        }
        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }
        public String getBaseQty() {
            return baseQty;
        }
        public void setBaseQty(String baseQty) {
            this.baseQty = baseQty;
        }
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }

        public String getPluBarCode() {
            return pluBarCode;
        }

        public void setPluBarCode(String pluBarCode) {
            this.pluBarCode = pluBarCode;
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
    public class level1ElmFileList{
        private String fileName;

        public String getFileName() {
            return fileName;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
