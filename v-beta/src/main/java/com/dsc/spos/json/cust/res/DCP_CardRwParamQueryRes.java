package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_CardRwParamQueryRes extends JsonRes
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

        private String groupId;//分组编码
        private String groupName;//分组名称
        private List<level2Elm> paramList;//

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

        public List<level2Elm> getParamList()
        {
            return paramList;
        }

        public void setParamList(List<level2Elm> paramList)
        {
            this.paramList = paramList;
        }
    }

    public class level2Elm
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

        public String getCurValue()
        {
            return curValue;
        }

        public void setCurValue(String curValue)
        {
            this.curValue = curValue;
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
}
