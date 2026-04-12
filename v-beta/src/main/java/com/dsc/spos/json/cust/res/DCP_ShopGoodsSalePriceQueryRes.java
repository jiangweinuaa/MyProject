package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/**
 * @apiNote 门店商品零售价查询
 * @since 2021-04-01
 * @author jinzma
 */
public class DCP_ShopGoodsSalePriceQueryRes extends JsonRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private List<level2Elm>goodsList;

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
        private String canUse;
        private String templateId;
        private String templateName;
        private String channelId;
        private String channelName;
        private String unit;
        private String unitName;
        private String price;
        private String minPrice;
        private String isDiscount;
        private String isProm;
        private String beginDate;
        private String endDate;

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

        public String getCanUse() {
            return canUse;
        }

        public void setCanUse(String canUse) {
            this.canUse = canUse;
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

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
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

        public String getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(String minPrice) {
            this.minPrice = minPrice;
        }

        public String getIsDiscount() {
            return isDiscount;
        }

        public void setIsDiscount(String isDiscount) {
            this.isDiscount = isDiscount;
        }

        public String getIsProm() {
            return isProm;
        }

        public void setIsProm(String isProm) {
            this.isProm = isProm;
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
