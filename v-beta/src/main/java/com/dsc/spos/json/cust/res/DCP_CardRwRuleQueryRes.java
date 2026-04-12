package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_CardRwRuleQueryRes extends JsonRes
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
        private String ruleId;//规则编码
        private String ruleName;//规则名称
        private String mediaType;//介质类型：RF/IC
        private String rwType;//操作类型：1-读写 2-只读
        private String priority;//优先级，小的优先
        private String status;//状态：-1未启用100已启用0-已禁用
        private String memo;//备注
        private String ruleType;//规则类型：1-通用2-专用
        private List<shopInfo> shopList;//

        public String getRuleId()
        {
            return ruleId;
        }

        public void setRuleId(String ruleId)
        {
            this.ruleId = ruleId;
        }

        public String getRuleName()
        {
            return ruleName;
        }

        public void setRuleName(String ruleName)
        {
            this.ruleName = ruleName;
        }

        public String getMediaType()
        {
            return mediaType;
        }

        public void setMediaType(String mediaType)
        {
            this.mediaType = mediaType;
        }

        public String getRwType()
        {
            return rwType;
        }

        public void setRwType(String rwType)
        {
            this.rwType = rwType;
        }

        public String getPriority()
        {
            return priority;
        }

        public void setPriority(String priority)
        {
            this.priority = priority;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getMemo()
        {
            return memo;
        }

        public void setMemo(String memo)
        {
            this.memo = memo;
        }

        public String getRuleType()
        {
            return ruleType;
        }

        public void setRuleType(String ruleType)
        {
            this.ruleType = ruleType;
        }

        public List<shopInfo> getShopList()
        {
            return shopList;
        }

        public void setShopList(List<shopInfo> shopList)
        {
            this.shopList = shopList;
        }
    }

    public class shopInfo
    {
        private String shopId;//门店编码
        private String shopName;//门店名称
        private String serialNo;//显示序号

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

        public String getSerialNo()
        {
            return serialNo;
        }

        public void setSerialNo(String serialNo)
        {
            this.serialNo = serialNo;
        }
    }


}
