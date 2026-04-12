package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;


public class DCP_PosFuncTemplateQueryReq extends JsonBasicReq
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
		private String status;//-1未启用100已启用0已禁用
		private String keyTxt;//编码/名称模糊搜索
				
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
						
	}
}
