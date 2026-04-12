package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PosFuncUpdateReq extends JsonBasicReq
{


	private levelRequest  request;
	
	
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
		private List<posFunc> datas;

		public List<posFunc> getDatas()
		{
			return datas;
		}

		public void setDatas(List<posFunc> datas)
		{
			this.datas = datas;
		}
		
		
		
		
	}
	

	public class posFunc 
	{				
		private String funcNo;
		private String funcName;
		private String shortcutKey;//快捷码
		private String icon;//图标
		private String qss;//样式
		private String priority;//显示顺序，排序第一列
		private String sortId;
		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getShortcutKey() {
			return shortcutKey;
		}
		public void setShortcutKey(String shortcutKey) {
			this.shortcutKey = shortcutKey;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public String getQss() {
			return qss;
		}
		public void setQss(String qss) {
			this.qss = qss;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
			
		
	}


}
