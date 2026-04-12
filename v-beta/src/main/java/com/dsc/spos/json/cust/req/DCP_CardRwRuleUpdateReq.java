package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_CardRwRuleUpdateReq extends JsonBasicReq
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
        private String ruleId;//规则编码，为空时表示创建，修改时必传
        private String ruleName;//规则名称
        private String mediaType;//介质类型：RF/IC
        private String rwType;//操作类型：1-读写 2-只读
        private String priority;//优先级，小的优先
        private String status;//状态：-1未启用100已启用0-已禁用
        private String memo;//备注
        private String ruleType;//规则类型：1-通用2-专用
        private List<shopInfo> shopList;//
        private List<paramInfo> params;//

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

        public List<paramInfo> getParams()
        {
            return params;
        }

        public void setParams(List<paramInfo> params)
        {
            this.params = params;
        }
    }


    public class shopInfo
    {
        private String shopId;//门店编码

        public String getShopId()
        {
            return shopId;
        }

        public void setShopId(String shopId)
        {
            this.shopId = shopId;
        }
    }

    public class paramListInfo
    {
        private String param;//参数编码
        private String curValue;//当前值

        public String getParam()
        {
            return param;
        }

        public void setParam(String param)
        {
            this.param = param;
        }

        public String getCurValue()
        {
            return curValue;
        }

        public void setCurValue(String curValue)
        {
            this.curValue = curValue;
        }
    }

    public class paramInfo
    {
        private String groupId;//分组编码
        private List<paramListInfo> paramList;//

        public String getGroupId()
        {
            return groupId;
        }

        public void setGroupId(String groupId)
        {
            this.groupId = groupId;
        }

        public List<paramListInfo> getParamList()
        {
            return paramList;
        }

        public void setParamList(List<paramListInfo> paramList)
        {
            this.paramList = paramList;
        }
    }

}
