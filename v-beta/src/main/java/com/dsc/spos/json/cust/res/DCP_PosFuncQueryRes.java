package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PosFuncQueryRes extends JsonRes
{

	private List<level1Elm> datas;
	
	
	public class level1Elm
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


	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
	
	
	
}
