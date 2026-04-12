package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_InvoiceOnLineSetReq extends JsonBasicReq {
    private Level1Elm request;
//    private String timestamp;

    public Level1Elm getRequest() {
        return request;
    }

    public void setRequest(Level1Elm request) {
        this.request = request;
    }

//    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }

    public class Level1Elm {
        private String templateId;
        private String templateName;
        private String enableInvoice;
        private String invDistrict;
        private Integer memberCarrier;
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

        public Integer getMemberCarrier() {
            return memberCarrier;
        }

        public void setMemberCarrier(Integer memberCarrier) {
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

    public class Channel {
        private String channelId;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }
    }

    public class App {
        private String appNo;

        public String getAppNo() {
            return appNo;
        }

        public void setAppNo(String appNo) {
            this.appNo = appNo;
        }
    }
}
