package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;


/**
 * 服務函數：StaffDelete
 *   說明：用户删除
 * 服务说明：用户删除
 * @author luoln 
 * @since  2017-02-21
 */
public class DCP_StaffDeleteReq extends JsonBasicReq{
	/**JSON Request
	 * 
	 	{
	 		"serviceId": "StaffDelete",	必傳且非空，服務名
			"token": "f14ee75ff5b220177ac0dc538bdea08c",	必傳且非空，訪問令牌
			"staffNO": "10001",	必傳且非空，用户编码	
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

		public String getStaffNo()
		{
			return staffNo;
		}

		public void setStaffNo(String staffNo)
		{
			this.staffNo = staffNo;
		}
	}
	
	
	
}
