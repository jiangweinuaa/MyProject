package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_ProcessTask0CreateReq extends JsonBasicReq
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
        private  String eId;
        private  String organizationNo;
        private  String opNo;
        private  String warehouse;
        private  String materialWarehouse;
        private  String pDate;
        private  String pTemplateNo;
        private  String memo;

        private List<level2> detailList;

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

        public String getOpNo()
        {
            return opNo;
        }

        public void setOpNo(String opNo)
        {
            this.opNo = opNo;
        }

        public String getWarehouse()
        {
            return warehouse;
        }

        public void setWarehouse(String warehouse)
        {
            this.warehouse = warehouse;
        }

        public String getMaterialWarehouse()
        {
            return materialWarehouse;
        }

        public void setMaterialWarehouse(String materialWarehouse)
        {
            this.materialWarehouse = materialWarehouse;
        }

        public String getpDate()
        {
            return pDate;
        }

        public void setpDate(String pDate)
        {
            this.pDate = pDate;
        }

        public String getpTemplateNo()
        {
            return pTemplateNo;
        }

        public void setpTemplateNo(String pTemplateNo)
        {
            this.pTemplateNo = pTemplateNo;
        }

        public String getMemo()
        {
            return memo;
        }

        public void setMemo(String memo)
        {
            this.memo = memo;
        }



        public List<level2> getDetailList()
        {
            return detailList;
        }

        public void setDetailList(List<level2> detailList)
        {
            this.detailList = detailList;
        }
    }


    public class level2
    {
        private  String item;
        private  String pluNo;
        private  String pluName;
        private  String pUnit;
        private  String adviceQty;
        private  String askQty;
        private  String pQty;
        private  String baseUnit;
        private  String baseQty;
        private  String unitRatio;
        private  String mulQty;
        private  String price;
        private  String amt;
        private  String distriPrice;
        private  String distriAmt;
        private  String dtNo1;
        private  String pQty1;
        private  String dtNo2;
        private  String pQty2;
        private  String dtNo3;
        private  String pQty3;
        private  String dtNo4;
        private  String pQty4;
        private  String dtNo5;
        private  String pQty5;
        private  String dtNo6;
        private  String pQty6;
        private  String dtNo7;
        private  String pQty7;
        private  String dtNo8;
        private  String pQty8;
        private  String dtNo9;
        private  String pQty9;
        private  String dtNo10;
        private  String pQty10;
        private  String featureNo;

        private List<level3> sourceList;



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

        public String getPluName()
        {
            return pluName;
        }

        public void setPluName(String pluName)
        {
            this.pluName = pluName;
        }

        public String getpUnit()
        {
            return pUnit;
        }

        public void setpUnit(String pUnit)
        {
            this.pUnit = pUnit;
        }

        public String getAdviceQty()
        {
            return adviceQty;
        }

        public void setAdviceQty(String adviceQty)
        {
            this.adviceQty = adviceQty;
        }

        public String getAskQty()
        {
            return askQty;
        }

        public void setAskQty(String askQty)
        {
            this.askQty = askQty;
        }

        public String getpQty()
        {
            return pQty;
        }

        public void setpQty(String pQty)
        {
            this.pQty = pQty;
        }

        public String getBaseUnit()
        {
            return baseUnit;
        }

        public void setBaseUnit(String baseUnit)
        {
            this.baseUnit = baseUnit;
        }

        public String getBaseQty()
        {
            return baseQty;
        }

        public void setBaseQty(String baseQty)
        {
            this.baseQty = baseQty;
        }

        public String getUnitRatio()
        {
            return unitRatio;
        }

        public void setUnitRatio(String unitRatio)
        {
            this.unitRatio = unitRatio;
        }

        public String getMulQty()
        {
            return mulQty;
        }

        public void setMulQty(String mulQty)
        {
            this.mulQty = mulQty;
        }

        public String getPrice()
        {
            return price;
        }

        public void setPrice(String price)
        {
            this.price = price;
        }

        public String getAmt()
        {
            return amt;
        }

        public void setAmt(String amt)
        {
            this.amt = amt;
        }

        public String getDistriPrice()
        {
            return distriPrice;
        }

        public void setDistriPrice(String distriPrice)
        {
            this.distriPrice = distriPrice;
        }

        public String getDistriAmt()
        {
            return distriAmt;
        }

        public void setDistriAmt(String distriAmt)
        {
            this.distriAmt = distriAmt;
        }

        public String getDtNo1()
        {
            return dtNo1;
        }

        public void setDtNo1(String dtNo1)
        {
            this.dtNo1 = dtNo1;
        }

        public String getpQty1()
        {
            return pQty1;
        }

        public void setpQty1(String pQty1)
        {
            this.pQty1 = pQty1;
        }

        public String getDtNo2()
        {
            return dtNo2;
        }

        public void setDtNo2(String dtNo2)
        {
            this.dtNo2 = dtNo2;
        }

        public String getpQty2()
        {
            return pQty2;
        }

        public void setpQty2(String pQty2)
        {
            this.pQty2 = pQty2;
        }

        public String getDtNo3()
        {
            return dtNo3;
        }

        public void setDtNo3(String dtNo3)
        {
            this.dtNo3 = dtNo3;
        }

        public String getpQty3()
        {
            return pQty3;
        }

        public void setpQty3(String pQty3)
        {
            this.pQty3 = pQty3;
        }

        public String getDtNo4()
        {
            return dtNo4;
        }

        public void setDtNo4(String dtNo4)
        {
            this.dtNo4 = dtNo4;
        }

        public String getpQty4()
        {
            return pQty4;
        }

        public void setpQty4(String pQty4)
        {
            this.pQty4 = pQty4;
        }

        public String getDtNo5()
        {
            return dtNo5;
        }

        public void setDtNo5(String dtNo5)
        {
            this.dtNo5 = dtNo5;
        }

        public String getpQty5()
        {
            return pQty5;
        }

        public void setpQty5(String pQty5)
        {
            this.pQty5 = pQty5;
        }

        public String getDtNo6()
        {
            return dtNo6;
        }

        public void setDtNo6(String dtNo6)
        {
            this.dtNo6 = dtNo6;
        }

        public String getpQty6()
        {
            return pQty6;
        }

        public void setpQty6(String pQty6)
        {
            this.pQty6 = pQty6;
        }

        public String getDtNo7()
        {
            return dtNo7;
        }

        public void setDtNo7(String dtNo7)
        {
            this.dtNo7 = dtNo7;
        }

        public String getpQty7()
        {
            return pQty7;
        }

        public void setpQty7(String pQty7)
        {
            this.pQty7 = pQty7;
        }

        public String getDtNo8()
        {
            return dtNo8;
        }

        public void setDtNo8(String dtNo8)
        {
            this.dtNo8 = dtNo8;
        }

        public String getpQty8()
        {
            return pQty8;
        }

        public void setpQty8(String pQty8)
        {
            this.pQty8 = pQty8;
        }

        public String getDtNo9()
        {
            return dtNo9;
        }

        public void setDtNo9(String dtNo9)
        {
            this.dtNo9 = dtNo9;
        }

        public String getpQty9()
        {
            return pQty9;
        }

        public void setpQty9(String pQty9)
        {
            this.pQty9 = pQty9;
        }

        public String getDtNo10()
        {
            return dtNo10;
        }

        public void setDtNo10(String dtNo10)
        {
            this.dtNo10 = dtNo10;
        }

        public String getpQty10()
        {
            return pQty10;
        }

        public void setpQty10(String pQty10)
        {
            this.pQty10 = pQty10;
        }

        public String getFeatureNo()
        {
            return featureNo;
        }

        public void setFeatureNo(String featureNo)
        {
            this.featureNo = featureNo;
        }

        public List<level3> getSourceList()
        {
            return sourceList;
        }

        public void setSourceList(List<level3> sourceList)
        {
            this.sourceList = sourceList;
        }
    }


    public class level3
    {
        private  String oType;
        private  String oFNo;
        private  String oShop;
        private  String oItem;
        private  String pluNo;
        private  String pUnit;
        private  String askQty;
        private  String shareQty;

        public String getoType()
        {
            return oType;
        }

        public void setoType(String oType)
        {
            this.oType = oType;
        }

        public String getoFNo()
        {
            return oFNo;
        }

        public void setoFNo(String oFNo)
        {
            this.oFNo = oFNo;
        }

        public String getoShop()
        {
            return oShop;
        }

        public void setoShop(String oShop)
        {
            this.oShop = oShop;
        }

        public String getoItem()
        {
            return oItem;
        }

        public void setoItem(String oItem)
        {
            this.oItem = oItem;
        }

        public String getPluNo()
        {
            return pluNo;
        }

        public void setPluNo(String pluNo)
        {
            this.pluNo = pluNo;
        }

        public String getpUnit()
        {
            return pUnit;
        }

        public void setpUnit(String pUnit)
        {
            this.pUnit = pUnit;
        }

        public String getAskQty()
        {
            return askQty;
        }

        public void setAskQty(String askQty)
        {
            this.askQty = askQty;
        }

        public String getShareQty()
        {
            return shareQty;
        }

        public void setShareQty(String shareQty)
        {
            this.shareQty = shareQty;
        }
    }


}
