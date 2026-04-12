package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PackageGoodsQuery_OpenReq extends JsonBasicReq
{
	private levelElm request;

	public levelElm getRequest()
	{
		return request;
	}

	public void setRequest(levelElm request)
	{
		this.request = request;
	}

	public class levelElm
	{				
		private List<Good> goodsList;
		private String type;//0:查询套餐 
		public List<Good> getGoodsList() {
			return goodsList;
		}
		public void setGoodsList(List<Good> goodsList) {
			this.goodsList = goodsList;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
								
	}
	
	public class Good{
		public String packagePluNo;

		public String getPackagePluNo() {
			return packagePluNo;
		}

		public void setPackagePluNo(String packagePluNo) {
			this.packagePluNo = packagePluNo;
		}
	}

}
