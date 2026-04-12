package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderDeliverySchedQueryRes extends JsonRes
{
	private List<level1Elm> datas;
	public class level1Elm
	{
		private String oEId;
		private String oShopId;
		private String deliveryType;
		private String priority;

		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}
		public String getoShopId() {
			return oShopId;
		}
		public void setoShopId(String oShopId) {
			this.oShopId = oShopId;
		}
		public String getDeliveryType() {
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType) {
			this.deliveryType = deliveryType;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
