package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/***
 * 多语言标签修改服务
 * @author 袁云洋 2019-01-17 
 *
 */
public class DCP_LangLabelUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String labelID;
		private String label_cn;
		private String label_tw;
		private String label_en;
		public String getLabelID() {
			return labelID;
		}
		public void setLabelID(String labelID) {
			this.labelID = labelID;
		}
		public String getLabel_cn() {
			return label_cn;
		}
		public void setLabel_cn(String label_cn) {
			this.label_cn = label_cn;
		}
		public String getLabel_tw() {
			return label_tw;
		}
		public void setLabel_tw(String label_tw) {
			this.label_tw = label_tw;
		}
		public String getLabel_en() {
			return label_en;
		}
		public void setLabel_en(String label_en) {
			this.label_en = label_en;
		}
	}
}
