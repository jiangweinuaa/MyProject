package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_PayMappingDeleteReq extends JsonBasicReq
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
        private List<level1Elm> infoList;

        public List<level1Elm> getInfoList() {
            return infoList;
        }

        public void setInfoList(List<level1Elm> infoList) {
            this.infoList = infoList;
        }
    }

    public class level1Elm
    {
        private String channelType;
        private String channelId;
        private String order_paycode;

        public String getChannelType() {
            return channelType;
        }

        public void setChannelType(String channelType) {
            this.channelType = channelType;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getOrder_paycode() {
            return order_paycode;
        }

        public void setOrder_paycode(String order_paycode) {
            this.order_paycode = order_paycode;
        }
    }
}
