package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_TareEnableReq extends JsonBasicReq
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

        /**
         * 修改状态，1启用 2禁用
         */
        private String oprType;
        private String[] tareList;

        public String getOprType()
        {
            return oprType;
        }

        public void setOprType(String oprType)
        {
            this.oprType = oprType;
        }

        public String[] getTareList()
        {
            return tareList;
        }

        public void setTareList(String[] tareList)
        {
            this.tareList = tareList;
        }
    }
}
