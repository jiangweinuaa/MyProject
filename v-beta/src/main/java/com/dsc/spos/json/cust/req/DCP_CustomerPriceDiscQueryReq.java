package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_CustomerPriceDiscQueryReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm
    {
        private String customerNo;
        private List<level2Elm> pluList;

        public String getCustomerNo() {
            return customerNo;
        }

        public void setCustomerNo(String customerNo) {
            this.customerNo = customerNo;
        }

        public List<level2Elm> getPluList() {
            return pluList;
        }

        public void setPluList(List<level2Elm> pluList) {
            this.pluList = pluList;
        }
    }

    public class level2Elm
    {
        private String pluNo;
        private String featureNo;

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
    }
}
