package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ProcessPlanCreateTaskReq extends JsonBasicReq
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
        private String eType ;//0不检查token，JOB调用
        private String eId;
        private String organizationNo;
        private String processPlanNo;
        private String pDateBegin;//起始生产日期（格式：20240408）
        private String pDateEnd;//截止生产日期（格式：20240408）

        public String geteType()
        {
            return eType;
        }

        public void seteType(String eType)
        {
            this.eType = eType;
        }

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

        public String getpDateBegin()
        {
            return pDateBegin;
        }

        public void setpDateBegin(String pDateBegin)
        {
            this.pDateBegin = pDateBegin;
        }

        public String getpDateEnd()
        {
            return pDateEnd;
        }

        public void setpDateEnd(String pDateEnd)
        {
            this.pDateEnd = pDateEnd;
        }
    }

}
