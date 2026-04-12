package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：ParaDefineDelete 說明：参数定义删除 服务说明：参数定义删除
 * 
 * @author jzma
 * @since 2017-03-06
 */
public class DCP_ParaDefineDeleteReq extends JsonBasicReq {
	/*
	 * "serviceId": "ParaDefineDelete", 必傳且非空，服務名 "token":
	 * "f14ee75ff5b220177ac0dc538bdea08c", 必傳且非空，訪問令牌 "item": "10001",
	 * 必傳且非空，参数编码
	 */

	private levelRequest request;

	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest {

		private String item;

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}
	}

}
