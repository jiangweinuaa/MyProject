package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class ISV_WMClientELMShopQueryRes extends JsonRes {
    private responseDatas datas;

    public responseDatas getDatas() {
        return datas;
    }

    public void setDatas(responseDatas datas) {
        this.datas = datas;
    }

    public class responseDatas
    {
        private String clientNo;
        List<level1Elm> datas;

        public String getClientNo() {
            return clientNo;
        }

        public void setClientNo(String clientNo) {
            this.clientNo = clientNo;
        }

        public List<level1Elm> getDatas() {
            return datas;
        }

        public void setDatas(List<level1Elm> datas) {
            this.datas = datas;
        }
    }
    public class level1Elm
    {
        private String channelId;
        private String orderShopNo;
        private String orderShopName;
        private String erpShopNo;
        private String erpShopName;
        private String mappingShopNo;
        private String appAuthToken;
        private String appKey;
        private String appSecret;
        private String appName;
        private String isTest;
        private String isJbp;
        private String userId;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getOrderShopNo() {
            return orderShopNo;
        }

        public void setOrderShopNo(String orderShopNo) {
            this.orderShopNo = orderShopNo;
        }

        public String getOrderShopName() {
            return orderShopName;
        }

        public void setOrderShopName(String orderShopName) {
            this.orderShopName = orderShopName;
        }

        public String getErpShopNo() {
            return erpShopNo;
        }

        public void setErpShopNo(String erpShopNo) {
            this.erpShopNo = erpShopNo;
        }

        public String getErpShopName() {
            return erpShopName;
        }

        public void setErpShopName(String erpShopName) {
            this.erpShopName = erpShopName;
        }

        public String getMappingShopNo() {
            return mappingShopNo;
        }

        public void setMappingShopNo(String mappingShopNo) {
            this.mappingShopNo = mappingShopNo;
        }

        public String getAppAuthToken() {
            return appAuthToken;
        }

        public void setAppAuthToken(String appAuthToken) {
            this.appAuthToken = appAuthToken;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getIsTest() {
            return isTest;
        }

        public void setIsTest(String isTest) {
            this.isTest = isTest;
        }

        public String getIsJbp() {
            return isJbp;
        }

        public void setIsJbp(String isJbp) {
            this.isJbp = isJbp;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
