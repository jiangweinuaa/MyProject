package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

/**
 * @apiNote 门店商品供货价查询
 * @since 2021-05-06
 * @author jinzma
 */
public class DCP_ShopGoodsSupPriceQueryRes extends JsonRes {

    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private List<level2Elm> goodsList;

        public List<level2Elm> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<level2Elm> goodsList) {
            this.goodsList = goodsList;
        }
    }
    public class level2Elm{
        private String pluNo;
        private String pluName;
        private String templateId;
        private String templateName;
        private String supplierType;
        private String supplierId;
        private String supplierName;
        private String unit;
        private String unitName;
        private String price;
        private String beginDate;
        private String endDate;
        private String canUse;

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

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public String getSupplierType() {
            return supplierType;
        }

        public void setSupplierType(String supplierType) {
            this.supplierType = supplierType;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
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

        public String getCanUse() {
            return canUse;
        }

        public void setCanUse(String canUse) {
            this.canUse = canUse;
        }
    }
}
