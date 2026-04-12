package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CounterQueryRes extends JsonRes
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
		private String counterNo;//
		private String counterName;//
		private String shopId;//
		private String shopName;//
		private String supplierId;//
		private String supplierName;//
		private String oprMode;//
		private String memo;//
		private List<counterNameLang> counterName_lang;//
		public String getCounterNo()
		{
			return counterNo;
		}
		public void setCounterNo(String counterNo)
		{
			this.counterNo = counterNo;
		}
		public String getCounterName()
		{
			return counterName;
		}
		public void setCounterName(String counterName)
		{
			this.counterName = counterName;
		}
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
		public String getSupplierId()
		{
			return supplierId;
		}
		public void setSupplierId(String supplierId)
		{
			this.supplierId = supplierId;
		}
		public String getSupplierName()
		{
			return supplierName;
		}
		public void setSupplierName(String supplierName)
		{
			this.supplierName = supplierName;
		}
		public String getOprMode()
		{
			return oprMode;
		}
		public void setOprMode(String oprMode)
		{
			this.oprMode = oprMode;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public List<counterNameLang> getCounterName_lang()
		{
			return counterName_lang;
		}
		public void setCounterName_lang(List<counterNameLang> counterName_lang)
		{
			this.counterName_lang = counterName_lang;
		}		
	}
	
	public class counterNameLang
	{
		private String langType;//
		private String name;//
		public String getLangType()
		{
			return langType;
		}
		public void setLangType(String langType)
		{
			this.langType = langType;
		}
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}		
	}
	
	
}
