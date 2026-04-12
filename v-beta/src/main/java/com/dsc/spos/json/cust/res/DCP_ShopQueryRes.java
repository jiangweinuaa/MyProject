package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 */
public class DCP_ShopQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String shopId;
		private String shopName;
		private String address;
		private String phone;
		private String in_cost_warehouse;
		private String in_non_cost_warehouse;
		private String inv_cost_warehouse;
		private String inv_non_cost_warehouse;
		private String out_cost_warehouse;
		private String out_non_cost_warehouse;
		
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getAddress()
		{
			return address;
		}
		public void setAddress(String address)
		{
			this.address = address;
		}
		public String getPhone()
		{
			return phone;
		}
		public void setPhone(String phone)
		{
			this.phone = phone;
		}
		public String getIn_cost_warehouse()
		{
			return in_cost_warehouse;
		}
		public void setIn_cost_warehouse(String in_cost_warehouse)
		{
			this.in_cost_warehouse = in_cost_warehouse;
		}
		public String getIn_non_cost_warehouse()
		{
			return in_non_cost_warehouse;
		}
		public void setIn_non_cost_warehouse(String in_non_cost_warehouse)
		{
			this.in_non_cost_warehouse = in_non_cost_warehouse;
		}
		public String getInv_cost_warehouse()
		{
			return inv_cost_warehouse;
		}
		public void setInv_cost_warehouse(String inv_cost_warehouse)
		{
			this.inv_cost_warehouse = inv_cost_warehouse;
		}
		public String getInv_non_cost_warehouse()
		{
			return inv_non_cost_warehouse;
		}
		public void setInv_non_cost_warehouse(String inv_non_cost_warehouse)
		{
			this.inv_non_cost_warehouse = inv_non_cost_warehouse;
		}
		public String getOut_cost_warehouse()
		{
			return out_cost_warehouse;
		}
		public void setOut_cost_warehouse(String out_cost_warehouse)
		{
			this.out_cost_warehouse = out_cost_warehouse;
		}
		public String getOut_non_cost_warehouse()
		{
			return out_non_cost_warehouse;
		}
		public void setOut_non_cost_warehouse(String out_non_cost_warehouse)
		{
			this.out_non_cost_warehouse = out_non_cost_warehouse;
		}		
		
		
	}
}	