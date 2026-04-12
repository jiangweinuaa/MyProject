package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_StockOutTemplateCreate
 *   說明：调拨模板删除
 * 服务说明：调拨模板删除
 * @author yuanyy 
 * @since  2019-12-18
 */
public class DCP_StockOutTemplateCreateReq extends JsonBasicReq{

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

	public class level1Elm{
		private String templateName;
		private String shopType;
		private String status;

		private List<level2Elm> datas;

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getShopType() {
			return shopType;
		}

		public void setShopType(String shopType) {
			this.shopType = shopType;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}

	}

	public class level2Elm{
		private String item;
		private String pluNo;
		private String pluName;
		private String punit;
		private String status;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		
		public String getPunit()
		{
			return punit;
		}
		public void setPunit(String punit)
		{
			this.punit = punit;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}


	}



}
