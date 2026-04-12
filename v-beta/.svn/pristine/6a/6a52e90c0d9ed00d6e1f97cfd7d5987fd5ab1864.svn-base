package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

import java.util.List;

public class DCP_CardRwRuleDetailRes extends JsonBasicRes
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

    public class paramListInfo
    {
        private String param;//参数编码
        private String name;//参数名称
        private String memo;//备注
        private String sortId;//显示顺序
        private String conType;//样式1-文本2-数字3下拉列表
        private String defValue;//默认值
        private String curValue;//当前值
        private String alterateValue;//备选值

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

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getMemo()
        {
            return memo;
        }

        public void setMemo(String memo)
        {
            this.memo = memo;
        }

        public String getSortId()
        {
            return sortId;
        }

        public void setSortId(String sortId)
        {
            this.sortId = sortId;
        }

        public String getConType()
        {
            return conType;
        }

        public void setConType(String conType)
        {
            this.conType = conType;
        }

        public String getDefValue()
        {
            return defValue;
        }

        public void setDefValue(String defValue)
        {
            this.defValue = defValue;
        }

        public String getAlterateValue()
        {
            return alterateValue;
        }

        public void setAlterateValue(String alterateValue)
        {
            this.alterateValue = alterateValue;
        }
    }

    public class paramInfo
    {
        private String groupId;//分组编码
        private String groupName;//分组名称
        private List<paramListInfo> paramList;//

        public String getGroupId()
        {
            return groupId;
        }

        public void setGroupId(String groupId)
        {
            this.groupId = groupId;
        }

        public String getGroupName()
        {
            return groupName;
        }

        public void setGroupName(String groupName)
        {
            this.groupName = groupName;
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
