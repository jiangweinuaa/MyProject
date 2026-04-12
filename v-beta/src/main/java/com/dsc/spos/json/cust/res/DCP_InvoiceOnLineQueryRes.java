package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_InvoiceOnLineQueryRes extends JsonRes {

    private Level1Elm datas;

    public Level1Elm getDatas() {
        return datas;
    }

    public void setDatas(Level1Elm datas) {
        this.datas = datas;
    }

    public static class Level1Elm {
        private String templateId;
        private String templateName;
        private String enableInvoice;
        private String invDistrict;
        private int memberCarrier;
        private String restrictChannel;
        private List<Channel> channelList;
        private List<App> appList;

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public String getEnableInvoice() {
            return enableInvoice;
        }

        public void setEnableInvoice(String enableInvoice) {
            this.enableInvoice = enableInvoice;
        }

        public String getInvDistrict() {
            return invDistrict;
        }

        public void setInvDistrict(String invDistrict) {
            this.invDistrict = invDistrict;
        }

        public int getMemberCarrier() {
            return memberCarrier;
        }

        public void setMemberCarrier(int memberCarrier) {
            this.memberCarrier = memberCarrier;
        }

        public String getRestrictChannel() {
            return restrictChannel;
        }

        public void setRestrictChannel(String restrictChannel) {
            this.restrictChannel = restrictChannel;
        }

        public List<Channel> getChannelList() {
            return channelList;
        }

        public void setChannelList(List<Channel> channelList) {
            this.channelList = channelList;
        }

        public List<App> getAppList() {
            return appList;
        }

        public void setAppList(List<App> appList) {
            this.appList = appList;
        }
    }

    public static class Channel {
        private String channelId;
        private String channelName;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }
    }

    public static class App {
        private String appNo;
        private String appName;

        public String getAppNo() {
            return appNo;
        }

        public void setAppNo(String appNo) {
            this.appNo = appNo;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }
    }
}
