package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_OrderDeliveryQueryRes extends JsonBasicRes
{
	private responseDatas datas;

	public responseDatas getDatas()
	{
		return datas;
	}

	public void setDatas(responseDatas datas)
	{
		this.datas = datas;
	}

	public class responseDatas
	{
		private String deliveryType; //物流类型 4-达达 24-圆通
		private String deliveryTypeName; //物流类型名称
		private String deliveryNo; //物流编号
		private String shortAddress;//圆通物流，三段码
		public String getDeliveryType()
		{
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType)
		{
			this.deliveryType = deliveryType;
		}
		public String getDeliveryTypeName()
		{
			return deliveryTypeName;
		}
		public void setDeliveryTypeName(String deliveryTypeName)
		{
			this.deliveryTypeName = deliveryTypeName;
		}
		public String getDeliveryNo()
		{
			return deliveryNo;
		}
		public void setDeliveryNo(String deliveryNo)
		{
			this.deliveryNo = deliveryNo;
		}

		public String getShortAddress() {
			return shortAddress;
		}

		public void setShortAddress(String shortAddress) {
			this.shortAddress = shortAddress;
		}
	}

}
