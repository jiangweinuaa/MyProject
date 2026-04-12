package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_SubStockTakeUpdate
 * 服务说明：盘点子任务修改
 * @author jinzma
 * @since  2021-02-25
 */
public class DCP_SubStockTakeUpdateReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String subStockTakeNo;
        private String memo;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }

        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
    }
}
