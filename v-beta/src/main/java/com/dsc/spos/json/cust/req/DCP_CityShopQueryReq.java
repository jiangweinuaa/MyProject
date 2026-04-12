package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CityShopQueryReq extends JsonBasicReq
{
    
    private levelRequest request;
    
    public levelRequest getRequest() {
        return request;
    }
    public void setRequest(levelRequest request) {
        this.request = request;
    }
    
    public class levelRequest
    {
        private String keyTxt;
        private String range;  //门店范围 0:全部门店 1:登入公司的所属门店 2:用户权限范围内门店和所属公司下的所有门店 3:用户权限范围内门店
        private String status;//-1未启用0.禁用100.已启用
        private String orgType;   //组织类型 0-直营  1-加盟(强)  2加盟（弱）
        private String businessType; //业务类型 0：支付设置处理加盟支付店号,其他业务不传
        private String[] orgForm;//组织类型 0-公司  1-组织  2-门店 3-其它
		public String[] getOrgForm() {
            return orgForm;
        }
        
        public void setOrgForm(String[] orgForm) {
            this.orgForm = orgForm;
        }
        
        public String getBusinessType() {
            return businessType;
        }
        
        public void setBusinessType(String businessType) {
            this.businessType = businessType;
        }
        
        public String getRange() {
            return range;
        }
        
        public void setRange(String range) {
            this.range = range;
        }
        
        public String getKeyTxt() {
            return keyTxt;
        }
        
        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getOrgType() {
            return orgType;
        }
        
        public void setOrgType(String orgType) {
            this.orgType = orgType;
        }
        
    }
    
    
    
    
}
