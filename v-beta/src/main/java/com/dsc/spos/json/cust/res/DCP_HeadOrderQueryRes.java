package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_HeadOrderQueryRes extends JsonRes
{

	private level1Elm datas;
	public level1Elm getDatas()
	{
		return datas;
	}
	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}

	public class level1Elm
	{
		private List<Order> orderList;

		public List<Order> getOrderList() {
			return orderList;
		}

		public void setOrderList(List<Order> orderList) {
			this.orderList = orderList;
		}
	}

	public class Order
	{
		private String eId;
		private String headOrderNo;
		private String orderNo;
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getHeadOrderNo() {
			return headOrderNo;
		}
		public void setHeadOrderNo(String headOrderNo) {
			this.headOrderNo = headOrderNo;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
	}

}
