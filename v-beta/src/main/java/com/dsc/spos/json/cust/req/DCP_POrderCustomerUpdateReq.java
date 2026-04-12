package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_POrderCustomerUpdateReq extends JsonBasicReq {
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String templateNo;
        private List<customer> customerList;

        public String getTemplateNo() {
            return templateNo;
        }

        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }

        public List<customer> getCustomerList() {
            return customerList;
        }

        public void setCustomerList(List<customer> customerList) {
            this.customerList = customerList;
        }
    }
    public class customer
    {
        private String customerNo;

        public String getCustomerNo() {
            return customerNo;
        }

        public void setCustomerNo(String customerNo) {
            this.customerNo = customerNo;
        }
    }


}
