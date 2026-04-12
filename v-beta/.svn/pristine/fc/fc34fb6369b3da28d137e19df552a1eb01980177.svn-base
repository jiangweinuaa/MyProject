package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：StaffShopGet
 *   說明：用户管辖门店信息查询
 * 服务说明：用户管辖门店信息查询
 * @author luoln 
 * @since  2017-03-02
 */
public class DCP_StaffShopQueryReq extends JsonBasicReq {
	/**JSON Request
	 * 
	  {	
		"serviceId": "StaffShopGet",	必傳且非空，服務名
		"token": "f14ee75ff5b220177ac0dc538bdea08c",	必傳且非空，訪問令牌
		"staffNO": "001",	必傳且非空，用户编码
	  }
	 * 
	**/
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

		private String keyTxt;
		
		
		public String getStaffNo()
		{
			return staffNo;
		}
		public void setStaffNo(String staffNo)
		{
			this.staffNo = staffNo;
		}
		public String getKeyTxt() {
		return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
		}

	}
	
}
