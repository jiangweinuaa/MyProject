package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CardRwRuleEnableReq extends JsonBasicReq
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

        private String oprType;//操作类型1-启用2-禁用

        public String[] getRuleIdList()
        {
            return ruleIdList;
        }

        public void setRuleIdList(String[] ruleIdList)
        {
            this.ruleIdList = ruleIdList;
        }

        public String getOprType()
        {
            return oprType;
        }

        public void setOprType(String oprType)
        {
            this.oprType = oprType;
        }
    }


}
