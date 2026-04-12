package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 外卖基础设置查询
 * @author yuanyy 2020-03-16
 *
 */
public class DCP_TakeOutOrderBaseSetQuery_OpenReq extends JsonBasicReq {

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
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	@Data
	public class level1Elm{
		private String keyTxt;
		private String shopId;
		private String eId;
		private level2Elm freight; // 配送费查询

		private String pageSize;
		private String pageNumber;

	}

	@Data
	public class level2Elm{
		private String longitude; // 经度
		private String latitude;  // 维度
		private String address; // 配送地址 (例如：上海市静安区某街道)
	}


}
