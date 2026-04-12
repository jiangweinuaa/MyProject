package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 服務函數：DCP_TicketStyleQuery
 * 服务说明：企业小票样式查询
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleQueryRes extends JsonBasicRes{
	
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
		private String ticketType; //小票类型编码
		private String ticketName; //小票类型名称
		
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
		public String getTicketName() {
			return ticketName;
		}
		public void setTicketName(String ticketName) {
			this.ticketName = ticketName;
		}
		
		
	}

}
