package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：PTemplateGet
 *    說明：要货模板查询
 * 服务说明：要货模板查询
 * @author jinzma 
 * @since  2017-03-09
 */
public class DCP_STakeTemplateDetailReq extends JsonBasicReq 
{
	
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
		private String templateNo;
		private String rangeWay;
		
		
		public String getTemplateNo()
		{
			return templateNo;
		}
		public void setTemplateNo(String templateNo)
		{
			this.templateNo = templateNo;
		}
		public String getRangeWay() {
			return rangeWay;
		}
		public void setRangeWay(String rangeWay) {
			this.rangeWay = rangeWay;
		}
	}
	

	

}

