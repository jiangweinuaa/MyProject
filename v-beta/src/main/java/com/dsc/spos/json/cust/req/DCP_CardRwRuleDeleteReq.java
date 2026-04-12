package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CardRwRuleDeleteReq extends JsonBasicReq
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
        private String[] ruleIdList;//规则编码

        public String[] getRuleIdList()
        {
            return ruleIdList;
        }

        public void setRuleIdList(String[] ruleIdList)
        {
            this.ruleIdList = ruleIdList;
        }
    }

}
