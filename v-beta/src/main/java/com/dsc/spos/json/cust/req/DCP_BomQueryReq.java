package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服务接口：BomGet 建立于2018-08-07
 * 
 * @author 24480
 *
 */
public class DCP_BomQueryReq extends JsonBasicReq
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

    @Data
	public class levelRequest
	{
		private String status;
		private String keyTxt;
		private String bomType;
		private String restrictShop;
		private String shopId;


        private String category;
        private String pGroupNo;
        private String materialPluNo;


	}

    @Data
    public class DcpCategory {
        private String eid;
        private String category;
        private String categoryLevel;
        private String upCategory;
    }

}
