package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 修改规格信息 2018-10-22
 * @author yuanyy
 *
 */
public class DCP_SpecUpdateReq extends JsonBasicReq {

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
		private String specNo;
		private String specName;
		private String status;
		
		
		public String getSpecNo()
		{
			return specNo;
		}
		public void setSpecNo(String specNo)
		{
			this.specNo = specNo;
		}
		public String getSpecName() {
			return specName;
		}
		public void setSpecName(String specName) {
			this.specName = specName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}
	

}
