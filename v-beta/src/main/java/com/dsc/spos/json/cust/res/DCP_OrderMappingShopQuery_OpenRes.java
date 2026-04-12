package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_ValidGoodsQuery_OpenRes.level2Barcode;
import com.dsc.spos.json.cust.res.DCP_ValidGoodsQuery_OpenRes.level2Feature;

public class DCP_OrderMappingShopQuery_OpenRes extends JsonRes
{
	private levelDatas datas;
	
	public levelDatas getDatas()
	{
		return datas;
	}

	public void setDatas(levelDatas datas)
	{
		this.datas = datas;
	}
	public class levelDatas
	{
		private List<level1Elm> orgList;

		public List<level1Elm> getOrgList()
		{
			return orgList;
		}

		public void setOrgList(List<level1Elm> orgList)
		{
			this.orgList = orgList;
		}

		
		
	}
	
	public class level1Elm
	{
		private String erpShopNo;
		private String erpShopName;
		private String orderShopNo;
		private String orderShopName;
		private String kdtId;
		public String getErpShopNo()
		{
			return erpShopNo;
		}
		public void setErpShopNo(String erpShopNo)
		{
			this.erpShopNo = erpShopNo;
		}
		public String getErpShopName()
		{
			return erpShopName;
		}
		public void setErpShopName(String erpShopName)
		{
			this.erpShopName = erpShopName;
		}
		public String getOrderShopNo()
		{
			return orderShopNo;
		}
		public void setOrderShopNo(String orderShopNo)
		{
			this.orderShopNo = orderShopNo;
		}
		public String getOrderShopName()
		{
			return orderShopName;
		}
		public void setOrderShopName(String orderShopName)
		{
			this.orderShopName = orderShopName;
		}
		public String getKdtId()
		{
			return kdtId;
		}
		public void setKdtId(String kdtId)
		{
			this.kdtId = kdtId;
		}
		
		
		
	}
	
	

}
