package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import java.util.List;

/**
 * 服务名称：DCP_SalePriceAdjustCreate
 * 服务说明：自建门店调价单新增(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustCreateReq extends JsonBasicReq {
    
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm{
        private String salePriceAdjustId;
        private String type;
        private List<level1Elm> datas;
        
        public String getSalePriceAdjustId() {
            return salePriceAdjustId;
        }
        public void setSalePriceAdjustId(String salePriceAdjustId) {
            this.salePriceAdjustId = salePriceAdjustId;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public List<level1Elm> getDatas() {
            return datas;
        }
        public void setDatas(List<level1Elm> datas) {
            this.datas = datas;
        }
    }
    public class level1Elm{
        private String item;
        private String pluNo;
        private String featureNo;
        private String unit;
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
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
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
