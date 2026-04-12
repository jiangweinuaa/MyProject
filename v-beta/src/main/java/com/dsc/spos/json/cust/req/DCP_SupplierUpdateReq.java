package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务名称：DCP_SupplierUpdate
 * 服务说明：供应商编辑
 * @author jinzma
 * @since  2022-03-15
 */
public class DCP_SupplierUpdateReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm{
        private String supplier;
        private String supplierName;
        private String abbr;
        private String mobile;
        private String address;
        private String selfBuiltShopId;
        private String status;
        
        public String getSupplier() {
            return supplier;
        }
        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }
        public String getSupplierName() {
            return supplierName;
        }
        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }
        public String getAbbr() {
            return abbr;
        }
        public void setAbbr(String abbr) {
            this.abbr = abbr;
        }
        public String getMobile() {
            return mobile;
        }
        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public String getSelfBuiltShopId() {
            return selfBuiltShopId;
        }
        public void setSelfBuiltShopId(String selfBuiltShopId) {
            this.selfBuiltShopId = selfBuiltShopId;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
