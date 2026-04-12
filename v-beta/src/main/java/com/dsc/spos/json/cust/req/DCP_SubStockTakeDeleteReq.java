package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_SubStockTakeDelete
 * 服务说明：盘点子任务删除
 * @author jinzma
 * @since  2021-02-25
 */
public class DCP_SubStockTakeDeleteReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String subStockTakeNo;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }
        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }
    }
}
