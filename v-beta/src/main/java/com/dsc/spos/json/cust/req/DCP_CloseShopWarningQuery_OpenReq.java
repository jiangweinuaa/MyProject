package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * @apiNote POS闭店提醒通知
 * @since 2021-05-21
 * @author jinzma
 */
public class DCP_CloseShopWarningQuery_OpenReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String eId;
        private String shopId;
        private String eDate;

        public String geteId() {
            return eId;
        }

        public void seteId(String eId) {
            this.eId = eId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String geteDate() {
            return eDate;
        }

        public void seteDate(String eDate) {
            this.eDate = eDate;
        }

    }
}
