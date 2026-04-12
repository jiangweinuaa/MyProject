package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 服務函數：DCP_TicketTypeQuery
 * 服务说明：小票类型查询
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketTypeQueryRes extends JsonBasicRes{
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String ticketType;// 小票类型编码
		private String ticketName; // 小票类型名称
		private String memo; // 备注
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
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		
	}
}
