package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PayMappingCreateReq extends JsonBasicReq
{
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public  class levelRequest
	{
        private String channelType;
        private String channelTypeName;
        private String channelId;
        private String channelIdName;
        private String payType;
        private String payName;
        private String order_paycode;
        private String order_payname;

        public String getChannelType() {
            return channelType;
        }

        public void setChannelType(String channelType) {
            this.channelType = channelType;
        }

        public String getChannelTypeName() {
            return channelTypeName;
        }

        public void setChannelTypeName(String channelTypeName) {
            this.channelTypeName = channelTypeName;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelIdName() {
            return channelIdName;
        }

        public void setChannelIdName(String channelIdName) {
            this.channelIdName = channelIdName;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getPayName() {
            return payName;
        }

        public void setPayName(String payName) {
            this.payName = payName;
        }

        public String getOrder_paycode() {
            return order_paycode;
        }

        public void setOrder_paycode(String order_paycode) {
            this.order_paycode = order_paycode;
        }

        public String getOrder_payname() {
            return order_payname;
        }

        public void setOrder_payname(String order_payname) {
            this.order_payname = order_payname;
        }
    }


}
