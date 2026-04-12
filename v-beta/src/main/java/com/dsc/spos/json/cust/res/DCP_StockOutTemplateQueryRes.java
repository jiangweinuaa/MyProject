package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 调拨模板查询  DCP_StockOutTemplateGetRes
 * @author yuanyy
 *
 */
public class DCP_StockOutTemplateQueryRes extends JsonRes {
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String templateNo;
		private String templateName;
		private String shopType;
		private String status;
		private String createDate;
		
		public String getTemplateNo() {
			return templateNo;
		}
		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}
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
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
	}
	
}