package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StaffShopUpdate
 *   說明：用户管辖门店更新
 * 服务说明：用户管辖门店更新
 * @author luoln 
 * @since  2017-03-02
 */
public class DCP_StaffShopUpdateReq extends JsonBasicReq {
	

	
	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String staffNo;
		private List<level1Elm> selectedShops;
		public String getStaffNo()
		{
			return staffNo;
		}
		public void setStaffNo(String staffNo)
		{
			this.staffNo = staffNo;
		}
		public List<level1Elm> getSelectedShops() {
			return selectedShops;
		}
		public void setSelectedShops(List<level1Elm> selectedShops) {
			this.selectedShops = selectedShops;
		}


	}
	
	public  class level1Elm
	{
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
	}
}
