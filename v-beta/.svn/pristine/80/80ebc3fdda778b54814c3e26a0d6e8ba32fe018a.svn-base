package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_CardReaderTypeQuery
 * 服务说明：读卡器类型查询
 * @author wangzyc
 * @since  2020-12-8
 */
public class DCP_CardReaderTypeQueryReq extends JsonBasicReq{
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String keyTxt; // 按编码 名称模糊检索

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}

}
