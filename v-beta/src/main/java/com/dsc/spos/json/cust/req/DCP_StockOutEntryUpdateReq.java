package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_StockOutEntryUpdate
 * 服务说明：退货录入修改
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryUpdateReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    @Data
    public class levelElm{
        private String stockOutEntryNo;
        private String memo;
        private String deliveryNo;
        private String warehouse;
        private String bsNo;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private String totCqty;
        private List<level1Elm> datas;
    }
    @Data
    public class level1Elm{
        private String item;
        private String pluNo;
        private String pluName;
        private String batchNo;
        private String prodDate;
        private String punit;
        private String pqty;
        private String unitRatio;
        private String price;
        private String distriPrice;
        private String amt;
        private String distriAmt;
        private String pluMemo;
        private String featureNo;
        private String stockQty;
        private String baseUnit;
        private String baseQty;
        private String bsNo;
        private String receiptOrg;
    }
}
