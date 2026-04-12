package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务名称：DCP_SalePriceAdjustDelete
 * 服务说明：自建门店调价单删除(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustDeleteReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm{
        private String salePriceAdjustNo;
        
        public String getSalePriceAdjustNo() {
            return salePriceAdjustNo;
        }
        public void setSalePriceAdjustNo(String salePriceAdjustNo) {
            this.salePriceAdjustNo = salePriceAdjustNo;
        }
    }
}
