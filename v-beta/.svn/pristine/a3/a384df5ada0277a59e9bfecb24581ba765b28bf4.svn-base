package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_PackageBySubProQuery_OpenReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private List<packageItem> packageItems;

        public List<packageItem> getPackageItems() {
            return packageItems;
        }

        public void setPackageItems(List<packageItem> packageItems) {
            this.packageItems = packageItems;
        }
    }
    public class packageItem
    {
        private String itemNo;

        public String getItemNo() {
            return itemNo;
        }

        public void setItemNo(String itemNo) {
            this.itemNo = itemNo;
        }
    }
}
