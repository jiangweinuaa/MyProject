package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_SalePriceTemplateGoodsEnableReq extends JsonBasicReq
{

    private levelRequest request;

    public void setRequest(levelRequest request)
    {
        this.request = request;
    }

    public levelRequest getRequest()
    {
        return request;
    }

    public class levelRequest
    {
        private String oprType ;//操作类型：1-启用2-禁用
        private String templateId ;//
        private List<plu> pluList;

        public String getOprType()
        {
            return oprType;
        }

        public void setOprType(String oprType)
        {
            this.oprType = oprType;
        }

        public String getTemplateId()
        {
            return templateId;
        }

        public void setTemplateId(String templateId)
        {
            this.templateId = templateId;
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
        private String item ;//
        private String pluNo ;//

        public String getItem()
        {
            return item;
        }

        public void setItem(String item)
        {
            this.item = item;
        }

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
