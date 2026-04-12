package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.math.BigDecimal;
import java.util.List;

public class DCP_TareQueryRes extends JsonRes
{
    private List<level1Elm> datas;

    public List<level1Elm> getDatas()
    {
        return datas;
    }

    public void setDatas(List<level1Elm> datas)
    {
        this.datas = datas;
    }

    public class level1Elm
    {
        private String tareId;
        private String tareName;
        private String unitType;
        private BigDecimal tare;
        private BigDecimal price;
        private String status;
        private String restrictShop;
        private List<levelShop> shopList;

        public String getTareId()
        {
            return tareId;
        }

        public void setTareId(String tareId)
        {
            this.tareId = tareId;
        }

        public String getTareName()
        {
            return tareName;
        }

        public void setTareName(String tareName)
        {
            this.tareName = tareName;
        }

        public String getUnitType()
        {
            return unitType;
        }

        public void setUnitType(String unitType)
        {
            this.unitType = unitType;
        }

        public BigDecimal getTare()
        {
            return tare;
        }

        public void setTare(BigDecimal tare)
        {
            this.tare = tare;
        }

        public BigDecimal getPrice()
        {
            return price;
        }

        public void setPrice(BigDecimal price)
        {
            this.price = price;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getRestrictShop()
        {
            return restrictShop;
        }

        public void setRestrictShop(String restrictShop)
        {
            this.restrictShop = restrictShop;
        }

        public List<levelShop> getShopList()
        {
            return shopList;
        }

        public void setShopList(List<levelShop> shopList)
        {
            this.shopList = shopList;
        }
    }

    public class levelShop
    {
        private String shopId;
        private String shopName;

        public String getShopId()
        {
            return shopId;
        }

        public void setShopId(String shopId)
        {
            this.shopId = shopId;
        }

        public String getShopName()
        {
            return shopName;
        }

        public void setShopName(String shopName)
        {
            this.shopName = shopName;
        }
    }


}
