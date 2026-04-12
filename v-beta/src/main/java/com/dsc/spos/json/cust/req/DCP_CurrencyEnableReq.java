package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_CurrencyEnableReq extends JsonBasicReq {
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String oprType;//操作类型：1-启用2-禁用
        private List<level1Elm> currencyList;

        public String getOprType() {
            return oprType;
        }
        public void setOprType(String oprType) {
            this.oprType = oprType;
        }


        public List<level1Elm> getCurrencyList() {
            return currencyList;
        }

        public void setCurrencyList(List<level1Elm> currencyList) {
            this.currencyList = currencyList;
        }
    }

    public class level1Elm
    {
        private String nation;
        private String currency;


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
    }
}
