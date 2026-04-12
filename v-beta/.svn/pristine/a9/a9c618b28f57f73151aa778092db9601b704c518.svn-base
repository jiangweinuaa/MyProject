package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 服務函數：DCP_TicketStyleDefQuery
 * 服务说明：系统小票样式查询
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleDefQueryRes extends JsonBasicRes{
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String styleId; // 样式编号
		private String styleName; // 样式名称
		private String ticketType; // 小票类型编码
		private String ticketName; // 小票类型名称
		private List<level2Elm> compoGroupList; // 小票类型组件分组
		
		public String getStyleId() {
			return styleId;
		}
		public void setStyleId(String styleId) {
			this.styleId = styleId;
		}
		public String getStyleName() {
			return styleName;
		}
		public void setStyleName(String styleName) {
			this.styleName = styleName;
		}
		public String getTicketType() {
			return ticketType;
		}
		public void setTicketType(String ticketType) {
			this.ticketType = ticketType;
		}
		public List<level2Elm> getCompoGroupList() {
			return compoGroupList;
		}
		public void setCompoGroupList(List<level2Elm> compoGroupList) {
			this.compoGroupList = compoGroupList;
		}
		public String getTicketName() {
			return ticketName;
		}
		public void setTicketName(String ticketName) {
			this.ticketName = ticketName;
		}
		
	}
	
	public class level2Elm{
		private String groupId; // 组件分组编码
		private String groupName; // 组件分组名称
		private List<level3Elm> compoList;
		
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public List<level3Elm> getCompoList() {
			return compoList;
		}
		public void setCompoList(List<level3Elm> compoList) {
			this.compoList = compoList;
		} 
		
	}
	
	public class level3Elm{
		private String compoId; // 组件编码
		private String compoName; // 组件名称
		private String conType; // 组件样式：1-文本2-数字3-单选
		private String defValue; // 默认值
		private String curValue; // 当前值
		private String alternatives; // 备选值
		
		public String getCompoId() {
			return compoId;
		}
		public void setCompoId(String compoId) {
			this.compoId = compoId;
		}
		public String getCompoName() {
			return compoName;
		}
		public void setCompoName(String compoName) {
			this.compoName = compoName;
		}
		public String getConType() {
			return conType;
		}
		public void setConType(String conType) {
			this.conType = conType;
		}
		public String getDefValue() {
			return defValue;
		}
		public void setDefValue(String defValue) {
			this.defValue = defValue;
		}
		public String getCurValue() {
			return curValue;
		}
		public void setCurValue(String curValue) {
			this.curValue = curValue;
		}
		public String getAlternatives() {
			return alternatives;
		}
		public void setAlternatives(String alternatives) {
			this.alternatives = alternatives;
		}
		
	}
}
