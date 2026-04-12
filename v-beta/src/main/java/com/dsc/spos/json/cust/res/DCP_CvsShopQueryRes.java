package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_CvsShopQueryRes extends JsonRes {

    private List<Level1Elm> datas;

    public List<Level1Elm> getDatas() {
        return datas;
    }
    public void setDatas(List<Level1Elm> datas) {
        this.datas = datas;
    }

    public static class Level1Elm {
        private String orgNo;
        private String orgName;
        private String famiShopId;
        private String hiLifeShopId;
        private String uniMartShopId;
        private String okShopId;
//        給前端使用的參數
        private String disable;

        public String getOrgNo() {
            return orgNo;
        }
        public void setOrgNo(String orgNo) {
            this.orgNo = orgNo;
        }
        public String getOrgName() {
            return orgName;
        }
        public void setOrgName(String orgName) {
            this.orgName = orgName;
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
        public String isDisable() {
            return disable;
        }
        public void setDisable(String disable) {
            this.disable = disable;
        }
    }
}
