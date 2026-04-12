package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DinnerMealUpdateReq extends JsonBasicReq
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
        private List<String> dinnerNo;

        private List<levelElmMeal> mealList;

        public List<String> getDinnerNo()
        {
            return dinnerNo;
        }

        public void setDinnerNo(List<String> dinnerNo)
        {
            this.dinnerNo = dinnerNo;
        }

        public List<levelElmMeal> getMealList()
        {
            return mealList;
        }

        public void setMealList(List<levelElmMeal> mealList)
        {
            this.mealList = mealList;
        }
    }


    public class levelElmMeal
    {
        private String pluNo;
        private String unitId;
        private String qty;
        private String defMode;


        public String getPluNo()
        {
            return pluNo;
        }

        public void setPluNo(String pluNo)
        {
            this.pluNo = pluNo;
        }

        public String getUnitId()
        {
            return unitId;
        }

        public void setUnitId(String unitId)
        {
            this.unitId = unitId;
        }

        public String getQty()
        {
            return qty;
        }

        public void setQty(String qty)
        {
            this.qty = qty;
        }

        public String getDefMode()
        {
            return defMode;
        }

        public void setDefMode(String defMode)
        {
            this.defMode = defMode;
        }
    }


}
