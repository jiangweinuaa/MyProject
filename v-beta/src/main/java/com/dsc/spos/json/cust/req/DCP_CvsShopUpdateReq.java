package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CvsShopUpdateReq extends JsonBasicReq {

    private Level1Elm request;

    public Level1Elm getRequest() {
        return request;
    }
    public void setRequest(Level1Elm request) {
        this.request = request;
    }

    public static class Level1Elm {
        private String orgNo;
        private String famiShopId;
        private String hiLifeShopId;
        private String uniMartShopId;
        private String okShopId;

        public String getOrgNo() {
            return orgNo;
        }
        public void setOrgNo(String orgNo) {
            this.orgNo = orgNo;
        }
        public String getFamiShopId() {
            return famiShopId;
        }
        public void setFamiShopId(String famiShopId) {
            this.famiShopId = famiShopId;
        }
        public String getHiLifeShopId() {
            return hiLifeShopId;
        }
        public void setHiLifeShopId(String hiLifeShopId) {
            this.hiLifeShopId = hiLifeShopId;
        }
        public String getUniMartShopId() {
            return uniMartShopId;
        }
        public void setUniMartShopId(String uniMartShopId) {
            this.uniMartShopId = uniMartShopId;
        }
        public String getOkShopId() {
            return okShopId;
        }
        public void setOkShopId(String okShopId) {
            this.okShopId = okShopId;
        }
    }
}
