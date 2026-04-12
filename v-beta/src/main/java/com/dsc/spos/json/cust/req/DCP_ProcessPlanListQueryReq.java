package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ProcessPlanListQueryReq extends JsonBasicReq
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
        private String keyTxt;
        private String status;
        private String dateType;//日期类型（0.单据日期 1.起始生产日期 2.截止生产日期）默认0
        private String beginDate;
        private String endDate;

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

        public String getKeyTxt()
        {
            return keyTxt;
        }

        public void setKeyTxt(String keyTxt)
        {
            this.keyTxt = keyTxt;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getDateType()
        {
            return dateType;
        }

        public void setDateType(String dateType)
        {
            this.dateType = dateType;
        }

        public String getBeginDate()
        {
            return beginDate;
        }

        public void setBeginDate(String beginDate)
        {
            this.beginDate = beginDate;
        }

        public String getEndDate()
        {
            return endDate;
        }

        public void setEndDate(String endDate)
        {
            this.endDate = endDate;
        }
    }

}
