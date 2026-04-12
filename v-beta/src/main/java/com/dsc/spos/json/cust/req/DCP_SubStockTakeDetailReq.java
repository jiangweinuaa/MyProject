package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_SubStockTakeDetail
 * 服务说明：盘点子任务详情
 * @author jinzma
 * @since  2021-02-26
 */
public class DCP_SubStockTakeDetailReq extends JsonBasicReq {
    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String subStockTakeNo;
        private String keyTxt;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }

        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }

        public String getKeyTxt() {
            return keyTxt;
        }

        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }
    }
}
