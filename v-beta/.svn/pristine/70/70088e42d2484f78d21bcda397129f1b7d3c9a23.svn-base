package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_TicketStyleCreate
 * 服务说明：企业小票样式创建
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleCreateReq extends JsonBasicReq{
	
	private level1Elm request;
	
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String styleId; // 样式编号
		private String styleName; // 样式名称
		private String ticketType; // 小票类型编码
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
		
	}
	public class level2Elm{
		private String groupId; // 组件分组编码
		private List<level3Elm> compoList;
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
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
		private String curValue; // 当前值
		public String getCompoId() {
			return compoId;
		}
		public void setCompoId(String compoId) {
			this.compoId = compoId;
		}
		public String getCurValue() {
			return curValue;
		}
		public void setCurValue(String curValue) {
			this.curValue = curValue;
		}
		
	}
	
}
