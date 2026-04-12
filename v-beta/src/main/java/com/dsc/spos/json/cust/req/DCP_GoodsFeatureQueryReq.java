package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：GoodsFeatureGetDCP 服务说明：商品明细获取
 * @author jinzma
 * @since 2019-07-15
 */
public class DCP_GoodsFeatureQueryReq extends JsonBasicReq {
    
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm {
        // SA 王欢需求 新增入参：pluType By wangzyc 20210726
        private String pluType;
        private String keyTxt;
        private String status;
        private String virtual;
        private String isBatch;
        
        private String[] pluBarcode;
        private String[] pluNo;
        private String[] category;
        private String[] brand;
        private String[] series;
        private String[] attrGroup;
        private String[] attrValue;
        private String billType;
        private String billNo;
        private String supplierId;
        private String templateType;
        private String templateNo;
        private String bringBackReceiptOrg;
        private String stockTakeNo;
        private String distriPriceRank;
        private String purchaseType;
        private String isHotGoods;
        private String isNewGoods;
        private String ISHTTPS;
        private String DomainName;
        private String CheckSuppGoods;
        private String IsStockMultipleUnit;
        private String selfBuiltShopId;
        private String searchScope;
        /**
         * 是否查询库存量Y/N
         */
        private String queryStockqty;
        private String queryStockWarehouse;
        private level1Elm stockTake;
        private String[] tag;
        private String isHeadStock;
        private String stockSort;
        
        
        public String getQueryStockqty()
        {
            return queryStockqty;
        }
        public void setQueryStockqty(String queryStockqty)
        {
            this.queryStockqty = queryStockqty;
        }
        public String getPluType() {
            return pluType;
        }
        public void setPluType(String pluType) {
            this.pluType = pluType;
        }
        public String getKeyTxt() {
            return keyTxt;
        }
        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getVirtual() {
            return virtual;
        }
        public void setVirtual(String virtual) {
            this.virtual = virtual;
        }
        public String getIsBatch() {
            return isBatch;
        }
        public void setIsBatch(String isBatch) {
            this.isBatch = isBatch;
        }
        public String getBillType() {
            return billType;
        }
        public void setBillType(String billType) {
            this.billType = billType;
        }
        public String getTemplateType() {
            return templateType;
        }
        public void setTemplateType(String templateType) {
            this.templateType = templateType;
        }
        public String getTemplateNo() {
            return templateNo;
        }
        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }
        public String getBillNo() {
            return billNo;
        }
        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }
        public String getSupplierId() {
            return supplierId;
        }
        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }
        public String[] getPluNo() {
            return pluNo;
        }
        public void setPluNo(String[] pluNo) {
            this.pluNo = pluNo;
        }
        public String[] getCategory() {
            return category;
        }
        public void setCategory(String[] category) {
            this.category = category;
        }
        public String[] getBrand() {
            return brand;
        }
        public void setBrand(String[] brand) {
            this.brand = brand;
        }
        public String[] getSeries() {
            return series;
        }
        public void setSeries(String[] series) {
            this.series = series;
        }
        public String[] getAttrGroup() {
            return attrGroup;
        }
        public void setAttrGroup(String[] attrGroup) {
            this.attrGroup = attrGroup;
        }
        public String[] getAttrValue() {
            return attrValue;
        }
        public void setAttrValue(String[] attrValue) {
            this.attrValue = attrValue;
        }
        public String getBringBackReceiptOrg() {
            return bringBackReceiptOrg;
        }
        public void setBringBackReceiptOrg(String bringBackReceiptOrg) {
            this.bringBackReceiptOrg = bringBackReceiptOrg;
        }
        public String getStockTakeNo() {
            return stockTakeNo;
        }
        public void setStockTakeNo(String stockTakeNo) {
            this.stockTakeNo = stockTakeNo;
        }
        public String getDistriPriceRank() {
            return distriPriceRank;
        }
        public void setDistriPriceRank(String distriPriceRank) {
            this.distriPriceRank = distriPriceRank;
        }
        public String getPurchaseType() {
            return purchaseType;
        }
        public void setPurchaseType(String purchaseType) {
            this.purchaseType = purchaseType;
        }
        public String[] getPluBarcode() {
            return pluBarcode;
        }
        public void setPluBarcode(String[] pluBarcode) {
            this.pluBarcode = pluBarcode;
        }
        public String getIsHotGoods() {
            return isHotGoods;
        }
        public void setIsHotGoods(String isHotGoods) {
            this.isHotGoods = isHotGoods;
        }
        public String getIsNewGoods() {
            return isNewGoods;
        }
        public void setIsNewGoods(String isNewGoods) {
            this.isNewGoods = isNewGoods;
        }
        public String getISHTTPS() {
            return ISHTTPS;
        }
        public void setISHTTPS(String ISHTTPS) {
            this.ISHTTPS = ISHTTPS;
        }
        public String getDomainName() {
            return DomainName;
        }
        public void setDomainName(String domainName) {
            DomainName = domainName;
        }
        public String getCheckSuppGoods() {
            return CheckSuppGoods;
        }
        public void setCheckSuppGoods(String checkSuppGoods) {
            CheckSuppGoods = checkSuppGoods;
        }
        public String getIsStockMultipleUnit() {
            return IsStockMultipleUnit;
        }
        public void setIsStockMultipleUnit(String isStockMultipleUnit) {
            IsStockMultipleUnit = isStockMultipleUnit;
        }
        public String getSelfBuiltShopId() {
            return selfBuiltShopId;
        }
        public void setSelfBuiltShopId(String selfBuiltShopId) {
            this.selfBuiltShopId = selfBuiltShopId;
        }
        public String getSearchScope() {
            return searchScope;
        }
        public void setSearchScope(String searchScope) {
            this.searchScope = searchScope;
        }
        public level1Elm getStockTake() {
            return stockTake;
        }
        public void setStockTake(level1Elm stockTake) {
            this.stockTake = stockTake;
        }
        public String[] getTag() {
            return tag;
        }
        public void setTag(String[] tag) {
            this.tag = tag;
        }
        public String getIsHeadStock() {
            return isHeadStock;
        }
        public void setIsHeadStock(String isHeadStock) {
            this.isHeadStock = isHeadStock;
        }
        public String getStockSort() {
            return stockSort;
        }
        public void setStockSort(String stockSort) {
            this.stockSort = stockSort;
        }
        public String getQueryStockWarehouse() {
            return queryStockWarehouse;
        }
        public void setQueryStockWarehouse(String queryStockWarehouse) {
            this.queryStockWarehouse = queryStockWarehouse;
        }
    }
    
    public class level1Elm{
        private String wareHouse;
        
        public String getWareHouse() {
            return wareHouse;
        }
        public void setWareHouse(String wareHouse) {
            this.wareHouse = wareHouse;
        }
    }
}
