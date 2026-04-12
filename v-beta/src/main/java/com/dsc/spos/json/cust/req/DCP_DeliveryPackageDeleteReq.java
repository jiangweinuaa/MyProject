package com.dsc.spos.json.cust.req;

/**
 * 货运包裹删除
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_DeliveryPackageDeleteReq 
{

	private levelRequest request;		

	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String packageNo;
		public String getPackageNo() {
			return packageNo;
		}
		public void setPackageNo(String packageNo) {
			this.packageNo = packageNo;
		}
	}


}
