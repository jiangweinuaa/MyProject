package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_StuffGoodsQueryRes extends JsonRes
{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String stuffId;//口味编码
		private String stuffName;//口味名称	
		private String pluNo;//商品编码
		private String pluName;//商品名称
		private String status;//分组编码
		
		public String getStuffId()
		{
			return stuffId;
		}
		public void setStuffId(String stuffId)
		{
			this.stuffId = stuffId;
		}
		public String getStuffName()
		{
			return stuffName;
		}
		public void setStuffName(String stuffName)
		{
			this.stuffName = stuffName;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
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
		
		
		

	
	}

	/*public class levelFlavorName
	{		
		private String langType;//
		private String name;//				

		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

	}*/

}
