package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_OrderMappingShopQueryRes.levelResponse;

public class DCP_OrderMappingShopCreateRes extends JsonRes 
{
    private levelResponse datas;
	
	public levelResponse getDatas()
	{
		return datas;
	}
	public void setDatas(levelResponse datas)
	{
		this.datas = datas;
	}

	public class levelResponse
	{
		private List<level1Elm> datas;
		
		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
  

	public class level1Elm
	{
		private String orderShopNo;
		private String orderShopName;
		private String erpShopNo;
		private String erpShopName;
		private String result;
		private String description;
			
		public String getOrderShopNo()
		{
			return orderShopNo;
		}
		public void setOrderShopNo(String orderShopNo)
		{
			this.orderShopNo = orderShopNo;
		}
		public String getErpShopNo()
		{
			return erpShopNo;
		}
		public void setErpShopNo(String erpShopNo)
		{
			this.erpShopNo = erpShopNo;
		}
		public String getOrderShopName() {
		return orderShopName;
		}
		public void setOrderShopName(String orderShopName) {
			this.orderShopName = orderShopName;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getErpShopName() {
			return erpShopName;
		}
		public void setErpShopName(String erpShopName) {
			this.erpShopName = erpShopName;
		}
		
					
	}

}
