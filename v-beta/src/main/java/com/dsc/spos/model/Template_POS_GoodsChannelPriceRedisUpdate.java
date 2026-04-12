package com.dsc.spos.model;

import java.util.List;

public class Template_POS_GoodsChannelPriceRedisUpdate
{

    private String templateId;

    private List<plu> pluList;


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

    public class plu
    {

        private String item;
        private String pluNo;

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
