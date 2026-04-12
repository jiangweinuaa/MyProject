package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ProcessPlanDetailQueryReq extends JsonBasicReq
{

    private levelElm request;

    public levelElm getRequest()
    {
        return request;
    }

    public void setRequest(levelElm request)
    {
        this.request = request;
    }

    public class levelElm
    {
        private String eId;
        private String organizationNo;
        private String processPlanNo;

        public String geteId()
        {
            return eId;
        }

        public void seteId(String eId)
        {
            this.eId = eId;
        }

        public String getOrganizationNo()
        {
            return organizationNo;
        }

        public void setOrganizationNo(String organizationNo)
        {
            this.organizationNo = organizationNo;
        }

        public String getProcessPlanNo()
        {
            return processPlanNo;
        }

        public void setProcessPlanNo(String processPlanNo)
        {
            this.processPlanNo = processPlanNo;
        }
    }




}
