package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/*
 * 服务函数:DCP_GoodsBarcodeQuery
 * 服务说明:商品开窗-条码
 * @author jinzma
 * @since  2020-08-18
 */
public class DCP_GoodsBarcodeQueryRes extends JsonRes{
    private List<level1Elm> datas;
    private List<illegal> illegalDatas;
    public List<illegal> getIllegalDatas() {
        return illegalDatas;
    }
    public void setIllegalDatas(List<illegal> illegalDatas) {
        this.illegalDatas = illegalDatas;
    }
    public List<level1Elm> getDatas() {
        return datas;
    }
    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }
    
    public class level1Elm{
        private String pluBarcode;
        private String pluNo;
        private String pluName;
        private String pluType;
        private String spec;
        private String featureNo;
        private String featureName;
        private String category;
        private String categoryName;
        private String warmType;
        private String virtual;
        private String stockManageType;
        private String memo;
        private String status;
        private String unit;
        private String unitName;
        private String unitRatio;
        private String unitUdLength;
        private String baseUnit;
        private String baseUnitName;
        private String baseUnitUdLength;
        private String price;
        private String distriPrice;
        private String weight;
        private String volume;
        private String isBatch;
        private String shelfLife;
        private String stockInValidDay;
        private String stockOutValidDay;
        private String checkValidDay;
        private String warningQty;
        private String safeQty;
        private String canPurchase;
        private String taxCode;
        private String canRequire;
        private String minQty;
        private String maxQty;
        private String mulQty;
        private String canRequireBack;
        private String canEstimate;
        private String clearType;
        private String qty;
        private String listImage;
        private String isHoliday;
        private String historyStockQty;
        private String punit;
        private String punitName;
        private String punitUdLength;
        private String punitRatio;
        private String pPrice;
        private String pDistriPrice;
        private String pSpec;
        private String maxOrderSpec;
        private String isNewGoods;
        private String isHotGoods;
        private String selfBuiltShopId;
        /**
         * 特征码汇总库存量，根据前端传参取值,默认999999
         */
        private String Stockqty;
        
        
        public String getStockqty()
        {
            return Stockqty;
        }
        public void setStockqty(String stockqty)
        {
            Stockqty = stockqty;
        }
        public String getPluBarcode() {
            return pluBarcode;
        }
        public void setPluBarcode(String pluBarcode) {
            this.pluBarcode = pluBarcode;
        }
        public String getPluNo() {
            return pluNo;
        }
        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }
        public String getPluName() {
            return pluName;
        }
        public void setPluName(String pluName) {
            this.pluName = pluName;
        }
        public String getPluType() {
            return pluType;
        }
        public void setPluType(String pluType) {
            this.pluType = pluType;
        }
        public String getSpec() {
            return spec;
        }
        public void setSpec(String spec) {
            this.spec = spec;
        }
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }
        public String getFeatureName() {
            return featureName;
        }
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getCategoryName() {
            return categoryName;
        }
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        public String getWarmType() {
            return warmType;
        }
        public void setWarmType(String warmType) {
            this.warmType = warmType;
        }
        public String getVirtual() {
            return virtual;
        }
        public void setVirtual(String virtual) {
            this.virtual = virtual;
        }
        public String getStockManageType() {
            return stockManageType;
        }
        public void setStockManageType(String stockManageType) {
            this.stockManageType = stockManageType;
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
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }
        public String getUnitName() {
            return unitName;
        }
        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }
        public String getUnitUdLength() {
            return unitUdLength;
        }
        public void setUnitUdLength(String unitUdLength) {
            this.unitUdLength = unitUdLength;
        }
        public String getBaseUnit() {
            return baseUnit;
        }
        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }
        public String getBaseUnitName() {
            return baseUnitName;
        }
        public void setBaseUnitName(String baseUnitName) {
            this.baseUnitName = baseUnitName;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getDistriPrice() {
            return distriPrice;
        }
        public void setDistriPrice(String distriPrice) {
            this.distriPrice = distriPrice;
        }
        public String getWeight() {
            return weight;
        }
        public void setWeight(String weight) {
            this.weight = weight;
        }
        public String getVolume() {
            return volume;
        }
        public void setVolume(String volume) {
            this.volume = volume;
        }
        public String getIsBatch() {
            return isBatch;
        }
        public void setIsBatch(String isBatch) {
            this.isBatch = isBatch;
        }
        public String getShelfLife() {
            return shelfLife;
        }
        public void setShelfLife(String shelfLife) {
            this.shelfLife = shelfLife;
        }
        public String getStockInValidDay() {
            return stockInValidDay;
        }
        public void setStockInValidDay(String stockInValidDay) {
            this.stockInValidDay = stockInValidDay;
        }
        public String getStockOutValidDay() {
            return stockOutValidDay;
        }
        public void setStockOutValidDay(String stockOutValidDay) {
            this.stockOutValidDay = stockOutValidDay;
        }
        public String getCheckValidDay() {
            return checkValidDay;
        }
        public void setCheckValidDay(String checkValidDay) {
            this.checkValidDay = checkValidDay;
        }
        public String getWarningQty() {
            return warningQty;
        }
        public void setWarningQty(String warningQty) {
            this.warningQty = warningQty;
        }
        public String getSafeQty() {
            return safeQty;
        }
        public void setSafeQty(String safeQty) {
            this.safeQty = safeQty;
        }
        public String getCanPurchase() {
            return canPurchase;
        }
        public void setCanPurchase(String canPurchase) {
            this.canPurchase = canPurchase;
        }
        public String getTaxCode() {
            return taxCode;
        }
        public void setTaxCode(String taxCode) {
            this.taxCode = taxCode;
        }
        public String getCanRequire() {
            return canRequire;
        }
        public void setCanRequire(String canRequire) {
            this.canRequire = canRequire;
        }
        public String getMinQty() {
            return minQty;
        }
        public void setMinQty(String minQty) {
            this.minQty = minQty;
        }
        public String getMaxQty() {
            return maxQty;
        }
        public void setMaxQty(String maxQty) {
            this.maxQty = maxQty;
        }
        public String getMulQty() {
            return mulQty;
        }
        public void setMulQty(String mulQty) {
            this.mulQty = mulQty;
        }
        public String getCanRequireBack() {
            return canRequireBack;
        }
        public void setCanRequireBack(String canRequireBack) {
            this.canRequireBack = canRequireBack;
        }
        public String getCanEstimate() {
            return canEstimate;
        }
        public void setCanEstimate(String canEstimate) {
            this.canEstimate = canEstimate;
        }
        public String getClearType() {
            return clearType;
        }
        public void setClearType(String clearType) {
            this.clearType = clearType;
        }
        public String getQty() {
            return qty;
        }
        public void setQty(String qty) {
            this.qty = qty;
        }
        public String getListImage() {
            return listImage;
        }
        public void setListImage(String listImage) {
            this.listImage = listImage;
        }
        public String getIsHoliday() {
            return isHoliday;
        }
        public void setIsHoliday(String isHoliday) {
            this.isHoliday = isHoliday;
        }
        public String getHistoryStockQty() {
            return historyStockQty;
        }
        public void setHistoryStockQty(String historyStockQty) {
            this.historyStockQty = historyStockQty;
        }
        public String getPunit() {
            return punit;
        }
        public void setPunit(String punit) {
            this.punit = punit;
        }
        public String getPunitName() {
            return punitName;
        }
        public void setPunitName(String punitName) {
            this.punitName = punitName;
        }
        public String getPunitUdLength() {
            return punitUdLength;
        }
        public void setPunitUdLength(String punitUdLength) {
            this.punitUdLength = punitUdLength;
        }
        public String getPunitRatio() {
            return punitRatio;
        }
        public void setPunitRatio(String punitRatio) {
            this.punitRatio = punitRatio;
        }
        public String getpPrice() {
            return pPrice;
        }
        public void setpPrice(String pPrice) {
            this.pPrice = pPrice;
        }
        public String getpDistriPrice() {
            return pDistriPrice;
        }
        public void setpDistriPrice(String pDistriPrice) {
            this.pDistriPrice = pDistriPrice;
        }
        public String getpSpec() {
            return pSpec;
        }
        public void setpSpec(String pSpec) {
            this.pSpec = pSpec;
        }
        public String getMaxOrderSpec()
        {
            return maxOrderSpec;
        }
        public void setMaxOrderSpec(String maxOrderSpec)
        {
            this.maxOrderSpec = maxOrderSpec;
        }
        public String getIsNewGoods() {
            return isNewGoods;
        }
        public void setIsNewGoods(String isNewGoods) {
            this.isNewGoods = isNewGoods;
        }
        public String getIsHotGoods() {
            return isHotGoods;
        }
        public void setIsHotGoods(String isHotGoods) {
            this.isHotGoods = isHotGoods;
        }
        public String getSelfBuiltShopId() {
            return selfBuiltShopId;
        }
        public void setSelfBuiltShopId(String selfBuiltShopId) {
            this.selfBuiltShopId = selfBuiltShopId;
        }
        public String getBaseUnitUdLength() {
            return baseUnitUdLength;
        }
        public void setBaseUnitUdLength(String baseUnitUdLength) {
            this.baseUnitUdLength = baseUnitUdLength;
        }
    }
    
    public class illegal{
        private String pluBarcode;
        
        public String getPluBarcode() {
            return pluBarcode;
        }
        public void setPluBarcode(String pluBarcode) {
            this.pluBarcode = pluBarcode;
        }
    }
}
