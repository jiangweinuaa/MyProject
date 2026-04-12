package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/**
 * 服务名称：DCP_SalePriceAdjustDetail
 * 服务说明：自建门店调价明细查询(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustDetailRes extends JsonRes {
    
    private List<level1Elm> datas;
    
    public List<level1Elm> getDatas() {
        return datas;
    }
    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }
    
    public class level1Elm{
        private String item;
        private String pluNo;
        private String pluName;
        private String unit;
        private String unitName;
        private String featureNo;
        private String featureName;
        private String standardPrice;
        private String price;
        private String minPrice;
        private String status;
        private String isProm;
        private String isDiscount;
        private String beginDate;
        private String endDate;
        
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
        public String getPluName() {
            return pluName;
        }
        public void setPluName(String pluName) {
            this.pluName = pluName;
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
        public String getStandardPrice() {
            return standardPrice;
        }
        public void setStandardPrice(String standardPrice) {
            this.standardPrice = standardPrice;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getMinPrice() {
            return minPrice;
        }
        public void setMinPrice(String minPrice) {
            this.minPrice = minPrice;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getIsProm() {
            return isProm;
        }
        public void setIsProm(String isProm) {
            this.isProm = isProm;
        }
        public String getIsDiscount() {
            return isDiscount;
        }
        public void setIsDiscount(String isDiscount) {
            this.isDiscount = isDiscount;
        }
        public String getBeginDate() {
            return beginDate;
        }
        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }
        public String getEndDate() {
            return endDate;
        }
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
    
    
    
}
