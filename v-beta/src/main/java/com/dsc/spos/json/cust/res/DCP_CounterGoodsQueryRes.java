package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CounterGoodsQueryRes extends JsonRes
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
		private String warehouse;//
		private String warehouseName;//
		private String pluBarcode;//
		private String pluNo;//
		private String pluName;//
		private String status;//
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
		public String getWarehouse()
		{
			return warehouse;
		}
		public void setWarehouse(String warehouse)
		{
			this.warehouse = warehouse;
		}
		public String getWarehouseName()
		{
			return warehouseName;
		}
		public void setWarehouseName(String warehouseName)
		{
			this.warehouseName = warehouseName;
		}
		public String getPluBarcode()
		{
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode)
		{
			this.pluBarcode = pluBarcode;
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
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}		
	}
	
}
