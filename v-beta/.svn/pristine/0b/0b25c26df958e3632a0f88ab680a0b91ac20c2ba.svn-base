package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.res.DCP_OrderParaQueryRes.responseDatas;

public class DCP_OrderCheckAgreeOrRejectRes extends JsonBasicRes
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
		private List<level1Elm> errorOrderList;

		public List<level1Elm> getErrorOrderList()
		{
			return errorOrderList;
		}

		public void setErrorOrderList(List<level1Elm> errorOrderList)
		{
			this.errorOrderList = errorOrderList;
		}
		
		
	}
	public class level1Elm 
	{
		private String orderNo;
		private String errorDesc;
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getErrorDesc()
		{
			return errorDesc;
		}
		public void setErrorDesc(String errorDesc)
		{
			this.errorDesc = errorDesc;
		}
		
		
	}
}
