package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CardRwRuleQueryReq extends JsonBasicReq
{
    private levelRequest request;

    public levelRequest getRequest()
    {
        return request;
    }

    public void setRequest(levelRequest request)
    {
        this.request = request;
    }

    public class levelRequest
    {
        private String status;//状态：-1未启用100已启用0-已禁用
        private String shopId;//门店编码

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getShopId()
        {
            return shopId;
        }

        public void setShopId(String shopId)
        {
            this.shopId = shopId;
        }
    }
}
