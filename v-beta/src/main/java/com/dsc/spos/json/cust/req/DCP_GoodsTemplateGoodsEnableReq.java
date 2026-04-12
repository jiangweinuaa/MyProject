package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_GoodsTemplateGoodsEnableReq extends JsonBasicReq
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
        private String templateId ;
        private String oprType ;
        private List<plu> pluList ;

        public String getTemplateId()
        {
            return templateId;
        }

        public void setTemplateId(String templateId)
        {
            this.templateId = templateId;
        }

        public String getOprType()
        {
            return oprType;
        }

        public void setOprType(String oprType)
        {
            this.oprType = oprType;
        }

        public List<plu> getPluList()
        {
            return pluList;
        }

        public void setPluList(List<plu> pluList)
        {
            this.pluList = pluList;
        }
    }

    public class plu
    {
        private String pluNo ;

        public String getPluNo()
        {
            return pluNo;
        }

        public void setPluNo(String pluNo)
        {
            this.pluNo = pluNo;
        }
    }

}
