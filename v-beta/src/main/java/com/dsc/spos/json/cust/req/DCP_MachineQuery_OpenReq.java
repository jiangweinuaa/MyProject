package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 查询所有机台信息，所有公司所有的门店的机台信息 （PAD导购用，有问题找产品）
 * @author yuanyy
 *
 */
public class DCP_MachineQuery_OpenReq extends JsonBasicReq {
	
	/**
	 * 2020-03-05 日志：上面催的急，后端先暂设规格字段。等产品把规格和流程设计好后再改
	 */
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}
	
	public class level1Elm
	{
		private String isRegister;
		private String producttype;

		public String getIsRegister() {
			return isRegister;
		}
		
		public void setIsRegister(String isRegister) {
			this.isRegister = isRegister;
		}

		public String getProducttype() {
			return producttype;
		}

		public void setProducttype(String producttype) {
			this.producttype = producttype;
		}
		
	}
	
	
	
}
