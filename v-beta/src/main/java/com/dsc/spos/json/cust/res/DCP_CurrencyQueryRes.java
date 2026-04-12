package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_CurrencyQueryRes extends JsonRes {
    private List<level1Elm> datas;

    public class level1Elm{
        private String nation;
        private String currency;

        private String currencyName;
        private String minValue;
        private String symbol;
        private Integer priceDigit;
        private Integer amountDigit;
        private Integer costPriceDigit;
        private Integer costAmountDigit;
        private String status;
        private List<level2Elm> currencyName_lang;

        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;



        public List<level2Elm> getCurrencyName_lang() {
            return currencyName_lang;
        }
        public void setCurrencyName_lang(List<level2Elm> currencyName_lang) {
            this.currencyName_lang = currencyName_lang;
        }


        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getMinValue() {
            return minValue;
        }

        public void setMinValue(String minValue) {
            this.minValue = minValue;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Integer getPriceDigit() {
            return priceDigit;
        }

        public void setPriceDigit(Integer priceDigit) {
            this.priceDigit = priceDigit;
        }

        public Integer getAmountDigit() {
            return amountDigit;
        }

        public void setAmountDigit(Integer amountDigit) {
            this.amountDigit = amountDigit;
        }

        public Integer getCostPriceDigit() {
            return costPriceDigit;
        }

        public void setCostPriceDigit(Integer costPriceDigit) {
            this.costPriceDigit = costPriceDigit;
        }


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getCostAmountDigit() {
            return costAmountDigit;
        }

        public void setCostAmountDigit(Integer costAmountDigit) {
            this.costAmountDigit = costAmountDigit;
        }

        public String getCurrencyName() {
            return currencyName;
        }

        public void setCurrencyName(String currencyName) {
            this.currencyName = currencyName;
        }

        public String getCreatorID() {
            return creatorID;
        }

        public void setCreatorID(String creatorID) {
            this.creatorID = creatorID;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getCreatorDeptID() {
            return creatorDeptID;
        }

        public void setCreatorDeptID(String creatorDeptID) {
            this.creatorDeptID = creatorDeptID;
        }

        public String getCreatorDeptName() {
            return creatorDeptName;
        }

        public void setCreatorDeptName(String creatorDeptName) {
            this.creatorDeptName = creatorDeptName;
        }

        public String getCreate_datetime() {
            return create_datetime;
        }

        public void setCreate_datetime(String create_datetime) {
            this.create_datetime = create_datetime;
        }

        public String getLastmodifyID() {
            return lastmodifyID;
        }

        public void setLastmodifyID(String lastmodifyID) {
            this.lastmodifyID = lastmodifyID;
        }

        public String getLastmodifyName() {
            return lastmodifyName;
        }

        public void setLastmodifyName(String lastmodifyName) {
            this.lastmodifyName = lastmodifyName;
        }

        public String getLastmodify_datetime() {
            return lastmodify_datetime;
        }

        public void setLastmodify_datetime(String lastmodify_datetime) {
            this.lastmodify_datetime = lastmodify_datetime;
        }
    }

    public List<level1Elm> getDatas() {
        return datas;
    }

    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }

    public class level2Elm{
        private String langType;
        private String name;

        //private String nation;

        public String getLangType() {
            return langType;
        }
        public void setLangType(String langType) {
            this.langType = langType;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }


    }
}
