package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CounterWarehouseQueryRes extends JsonRes
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
		private String deductionRate;//
		private String useType;//
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
		public String getDeductionRate()
		{
			return deductionRate;
		}
		public void setDeductionRate(String deductionRate)
		{
			this.deductionRate = deductionRate;
		}
		public String getUseType()
		{
			return useType;
		}
		public void setUseType(String useType)
		{
			this.useType = useType;
		}


	}

}
