package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_OrderBasicSettingQueryRes extends JsonBasicRes
{

	private responseDatas datas;
	
	
	
	public responseDatas getDatas()
	{
		return datas;
	}

	public void setDatas(responseDatas datas)
	{
		this.datas = datas;
	}

	public class responseDatas
	{
		private List<level1Elm> settingList;

		public List<level1Elm> getSettingList()
		{
			return settingList;
		}

		public void setSettingList(List<level1Elm> settingList)
		{
			this.settingList = settingList;
		}
		
	}
	
	public class level1Elm
	{
		private String settingNo;
		private String settingName;
		private String settingValue;
		private String conType;//控件类型（1.文本格式 2.数字格式 3.日期格式 4.时间格式 5.下拉框）
		private String selectType;//选择类型（0单选；1复选）
		public String getSettingNo()
		{
			return settingNo;
		}
		public void setSettingNo(String settingNo)
		{
			this.settingNo = settingNo;
		}
		public String getSettingName()
		{
			return settingName;
		}
		public void setSettingName(String settingName)
		{
			this.settingName = settingName;
		}
		public String getSettingValue()
		{
			return settingValue;
		}
		public void setSettingValue(String settingValue)
		{
			this.settingValue = settingValue;
		}
		public String getConType()
		{
			return conType;
		}
		public void setConType(String conType)
		{
			this.conType = conType;
		}
		public String getSelectType()
		{
			return selectType;
		}
		public void setSelectType(String selectType)
		{
			this.selectType = selectType;
		}
		
		
		
	}
	
}
