package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_ProcessTask0DetailQueryRes extends JsonRes
{


    private level1Elm datas;

    public level1Elm getDatas()
    {
        return datas;
    }

    public void setDatas(level1Elm datas)
    {
        this.datas = datas;
    }

    public class level1Elm
    {
        private List<level2Elm> dataList;

        public List<level2Elm> getDataList()
        {
            return dataList;
        }

        public void setDataList(List<level2Elm> dataList)
        {
            this.dataList = dataList;
        }
    }
    public class level2Elm
    {
        private int item;
        private String pluNo;
        private String pluName;
        private String spec;
        private String pUnit;
        private String unitUdLength;
        private  double adviceQty;
        private  double askQty;
        private String pUName;
        private double pQty;
        private  String baseUnit;
        private String baseUnitUdLength;
        private  String baseQty;
        private  String unitRatio;
        private  String mulQty;
        private String price;
        private String amt;
        private String distriPrice;
        private String distriAmt;
        private String dtNo1;
        private String dtName1;
        private String dtBeginTime1;
        private String dtEndTime1;
        private String pQty1;
        private String dtNo2;
        private String dtName2;
        private String dtBeginTime2;
        private String dtEndTime2;
        private String pQty2;
        private String dtNo3;
        private String dtName3;
        private String dtBeginTime3;
        private String dtEndTime3;
        private String pQty3;
        private String dtNo4;
        private String dtName4;
        private String dtBeginTime4;
        private String dtEndTime4;
        private String pQty4;
        private String dtNo5;
        private String dtName5;
        private String dtBeginTime5;
        private String dtEndTime5;
        private String pQty5;
        private String dtNo6;
        private String dtName6;
        private String dtBeginTime6;
        private String dtEndTime6;
        private String pQty6;
        private String dtNo7;
        private String dtName7;
        private String dtBeginTime7;
        private String dtEndTime7;
        private String pQty7;
        private String dtNo8;
        private String dtName8;
        private String dtBeginTime8;
        private String dtEndTime8;
        private String pQty8;
        private String dtNo9;
        private String dtName9;
        private String dtBeginTime9;
        private String dtEndTime9;
        private String pQty9;
        private String dtNo10;
        private String dtName10;
        private String dtBeginTime10;
        private String dtEndTime10;
        private String pQty10;
        private String featureNo;
        private String featureName;
        private String baseUnitName;

        public String getBaseUnitName()
        {
            return baseUnitName;
        }

        public void setBaseUnitName(String baseUnitName)
        {
            this.baseUnitName = baseUnitName;
        }

        private List<level3Elm> sourceList;



        public int getItem()
        {
            return item;
        }

        public void setItem(int item)
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

        public String getSpec()
        {
            return spec;
        }

        public void setSpec(String spec)
        {
            this.spec = spec;
        }

        public String getpUnit()
        {
            return pUnit;
        }

        public void setpUnit(String pUnit)
        {
            this.pUnit = pUnit;
        }

        public String getUnitUdLength()
        {
            return unitUdLength;
        }

        public void setUnitUdLength(String unitUdLength)
        {
            this.unitUdLength = unitUdLength;
        }

        public double getAdviceQty()
        {
            return adviceQty;
        }

        public void setAdviceQty(double adviceQty)
        {
            this.adviceQty = adviceQty;
        }

        public double getAskQty()
        {
            return askQty;
        }

        public void setAskQty(double askQty)
        {
            this.askQty = askQty;
        }

        public String getBaseUnitUdLength()
        {
            return baseUnitUdLength;
        }

        public void setBaseUnitUdLength(String baseUnitUdLength)
        {
            this.baseUnitUdLength = baseUnitUdLength;
        }

        public String getpUName()
        {
            return pUName;
        }

        public void setpUName(String pUName)
        {
            this.pUName = pUName;
        }

        public double getpQty()
        {
            return pQty;
        }

        public void setpQty(double pQty)
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

        public String getDtName1()
        {
            return dtName1;
        }

        public void setDtName1(String dtName1)
        {
            this.dtName1 = dtName1;
        }

        public String getDtBeginTime1()
        {
            return dtBeginTime1;
        }

        public void setDtBeginTime1(String dtBeginTime1)
        {
            this.dtBeginTime1 = dtBeginTime1;
        }

        public String getDtEndTime1()
        {
            return dtEndTime1;
        }

        public void setDtEndTime1(String dtEndTime1)
        {
            this.dtEndTime1 = dtEndTime1;
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

        public String getDtName2()
        {
            return dtName2;
        }

        public void setDtName2(String dtName2)
        {
            this.dtName2 = dtName2;
        }

        public String getDtBeginTime2()
        {
            return dtBeginTime2;
        }

        public void setDtBeginTime2(String dtBeginTime2)
        {
            this.dtBeginTime2 = dtBeginTime2;
        }

        public String getDtEndTime2()
        {
            return dtEndTime2;
        }

        public void setDtEndTime2(String dtEndTime2)
        {
            this.dtEndTime2 = dtEndTime2;
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

        public String getDtName3()
        {
            return dtName3;
        }

        public void setDtName3(String dtName3)
        {
            this.dtName3 = dtName3;
        }

        public String getDtBeginTime3()
        {
            return dtBeginTime3;
        }

        public void setDtBeginTime3(String dtBeginTime3)
        {
            this.dtBeginTime3 = dtBeginTime3;
        }

        public String getDtEndTime3()
        {
            return dtEndTime3;
        }

        public void setDtEndTime3(String dtEndTime3)
        {
            this.dtEndTime3 = dtEndTime3;
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

        public String getDtName4()
        {
            return dtName4;
        }

        public void setDtName4(String dtName4)
        {
            this.dtName4 = dtName4;
        }

        public String getDtBeginTime4()
        {
            return dtBeginTime4;
        }

        public void setDtBeginTime4(String dtBeginTime4)
        {
            this.dtBeginTime4 = dtBeginTime4;
        }

        public String getDtEndTime4()
        {
            return dtEndTime4;
        }

        public void setDtEndTime4(String dtEndTime4)
        {
            this.dtEndTime4 = dtEndTime4;
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

        public String getDtName5()
        {
            return dtName5;
        }

        public void setDtName5(String dtName5)
        {
            this.dtName5 = dtName5;
        }

        public String getDtBeginTime5()
        {
            return dtBeginTime5;
        }

        public void setDtBeginTime5(String dtBeginTime5)
        {
            this.dtBeginTime5 = dtBeginTime5;
        }

        public String getDtEndTime5()
        {
            return dtEndTime5;
        }

        public void setDtEndTime5(String dtEndTime5)
        {
            this.dtEndTime5 = dtEndTime5;
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

        public String getDtName6()
        {
            return dtName6;
        }

        public void setDtName6(String dtName6)
        {
            this.dtName6 = dtName6;
        }

        public String getDtBeginTime6()
        {
            return dtBeginTime6;
        }

        public void setDtBeginTime6(String dtBeginTime6)
        {
            this.dtBeginTime6 = dtBeginTime6;
        }

        public String getDtEndTime6()
        {
            return dtEndTime6;
        }

        public void setDtEndTime6(String dtEndTime6)
        {
            this.dtEndTime6 = dtEndTime6;
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

        public String getDtName7()
        {
            return dtName7;
        }

        public void setDtName7(String dtName7)
        {
            this.dtName7 = dtName7;
        }

        public String getDtBeginTime7()
        {
            return dtBeginTime7;
        }

        public void setDtBeginTime7(String dtBeginTime7)
        {
            this.dtBeginTime7 = dtBeginTime7;
        }

        public String getDtEndTime7()
        {
            return dtEndTime7;
        }

        public void setDtEndTime7(String dtEndTime7)
        {
            this.dtEndTime7 = dtEndTime7;
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

        public String getDtName8()
        {
            return dtName8;
        }

        public void setDtName8(String dtName8)
        {
            this.dtName8 = dtName8;
        }

        public String getDtBeginTime8()
        {
            return dtBeginTime8;
        }

        public void setDtBeginTime8(String dtBeginTime8)
        {
            this.dtBeginTime8 = dtBeginTime8;
        }

        public String getDtEndTime8()
        {
            return dtEndTime8;
        }

        public void setDtEndTime8(String dtEndTime8)
        {
            this.dtEndTime8 = dtEndTime8;
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

        public String getDtName9()
        {
            return dtName9;
        }

        public void setDtName9(String dtName9)
        {
            this.dtName9 = dtName9;
        }

        public String getDtBeginTime9()
        {
            return dtBeginTime9;
        }

        public void setDtBeginTime9(String dtBeginTime9)
        {
            this.dtBeginTime9 = dtBeginTime9;
        }

        public String getDtEndTime9()
        {
            return dtEndTime9;
        }

        public void setDtEndTime9(String dtEndTime9)
        {
            this.dtEndTime9 = dtEndTime9;
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

        public String getDtName10()
        {
            return dtName10;
        }

        public void setDtName10(String dtName10)
        {
            this.dtName10 = dtName10;
        }

        public String getDtBeginTime10()
        {
            return dtBeginTime10;
        }

        public void setDtBeginTime10(String dtBeginTime10)
        {
            this.dtBeginTime10 = dtBeginTime10;
        }

        public String getDtEndTime10()
        {
            return dtEndTime10;
        }

        public void setDtEndTime10(String dtEndTime10)
        {
            this.dtEndTime10 = dtEndTime10;
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

        public String getFeatureName()
        {
            return featureName;
        }

        public void setFeatureName(String featureName)
        {
            this.featureName = featureName;
        }

        public List<level3Elm> getSourceList()
        {
            return sourceList;
        }

        public void setSourceList(List<level3Elm> sourceList)
        {
            this.sourceList = sourceList;
        }
    }


    public class level3Elm
    {
        private String mItem;
        private String oType;
        private String oFNo;
        private String oShop;
        private String oItem;
        private String pluNo;
        private String pUnit;
        private String pUName;
        private String unitUdLength;
        private double askQty;
        private double shareQty;
        private String oShopName;


        public String getmItem()
        {
            return mItem;
        }

        public void setmItem(String mItem)
        {
            this.mItem = mItem;
        }

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

        public String getpUName()
        {
            return pUName;
        }

        public void setpUName(String pUName)
        {
            this.pUName = pUName;
        }

        public String getUnitUdLength()
        {
            return unitUdLength;
        }

        public void setUnitUdLength(String unitUdLength)
        {
            this.unitUdLength = unitUdLength;
        }

        public double getAskQty()
        {
            return askQty;
        }

        public void setAskQty(double askQty)
        {
            this.askQty = askQty;
        }

        public double getShareQty()
        {
            return shareQty;
        }

        public void setShareQty(double shareQty)
        {
            this.shareQty = shareQty;
        }

        public String getoShopName()
        {
            return oShopName;
        }

        public void setoShopName(String oShopName)
        {
            this.oShopName = oShopName;
        }
    }


}
