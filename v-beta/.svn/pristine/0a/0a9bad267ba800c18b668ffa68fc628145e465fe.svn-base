package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 对账导入功能
 * @author 08546
 *
 */
public class DCP_OrderStatementImportCreateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{

		/**
		 * 文件名
		 */
		private String excelFileName;
		/**
		 * 必传，对账类型 1-饿了么 2-美团 8-京东 等
		 */
		private String thirdType;

		public String getThirdType() {
			return thirdType;
		}

		public void setThirdType(String thirdType) {
			this.thirdType = thirdType;
		}

		public String getExcelFileName() {
			return excelFileName;
		}

		public void setExcelFileName(String excelFileName) {
			this.excelFileName = excelFileName;
		}
	}
}
