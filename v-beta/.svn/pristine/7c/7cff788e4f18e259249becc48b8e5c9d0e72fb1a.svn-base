package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务函数：DCP_StockOutEntryQuery
 * 服务说明：退货录入查询
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryQueryReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    @Data
    public class levelElm{
        private String beginDate;
        private String endDate;
        private String status;
        private String keyTxt;
    }
}
