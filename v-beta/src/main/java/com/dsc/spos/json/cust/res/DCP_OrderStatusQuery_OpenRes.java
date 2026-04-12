package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 订单状态查询
 * @author Huawei
 *
 */
public class DCP_OrderStatusQuery_OpenRes extends JsonBasicRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String orderNO;
		private String shipType;
		private List<level2Elm> statusDatas;
		public String getOrderNO() {
			return orderNO;
		}
		public String getShipType() {
			return shipType;
		}
		public List<level2Elm> getStatusDatas() {
			return statusDatas;
		}
		public void setOrderNO(String orderNO) {
			this.orderNO = orderNO;
		}
		public void setShipType(String shipType) {
			this.shipType = shipType;
		}
		public void setStatusDatas(List<level2Elm> statusDatas) {
			this.statusDatas = statusDatas;
		}
		
	}
	
	public class level2Elm{
		
		private String status;
		private String dateTime;
		private String isNowStatus;
		
		public String getStatus() {
			return status;
		}
		public String getDateTime() {
			return dateTime;
		}
		public String getIsNowStatus() {
			return isNowStatus;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}
		public void setIsNowStatus(String isNowStatus) {
			this.isNowStatus = isNowStatus;
		}
		
	}
	
}
