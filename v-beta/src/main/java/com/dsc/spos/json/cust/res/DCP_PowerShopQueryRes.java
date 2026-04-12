package com.dsc.spos.json.cust.res;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_UnitQueryRes.level1Elm;

/**
 * 服務函數：PowerShopGet
 *    說明：门店查询
 * 服务说明：门店查询
 * @author ycl 
 * @since  2017-03-09
 */
public class DCP_PowerShopQueryRes extends JsonRes{
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
		private String telephone;
		private String address;
		private String in_cost_warehouse;
		private String in_non_cost_warehouse;
		private String out_cost_warehouse;
		private String out_non_cost_warehouse;
		private String inv_cost_warehouse;
		private String inv_non_cost_warehouse;		

		private String in_cost_warehouse_name;
		private String in_non_cost_warehouse_name;
		private String out_cost_warehouse_name;
		private String out_non_cost_warehouse_name;
		private String inv_cost_warehouse_name;
		private String inv_non_cost_warehouse_name;

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

		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}

		public String getIn_cost_warehouse() {
			return in_cost_warehouse;
		}
		public void setIn_cost_warehouse(String in_cost_warehouse) {
			this.in_cost_warehouse = in_cost_warehouse;
		}

		public String getIn_non_cost_warehouse() {
			return in_non_cost_warehouse;
		}
		public void setIn_non_cost_warehouse(String in_non_cost_warehouse) {
			this.in_non_cost_warehouse = in_non_cost_warehouse;
		}

		public String getOut_cost_warehouse() {
			return out_cost_warehouse;
		}
		public void setOut_cost_warehouse(String out_cost_warehouse) {
			this.out_cost_warehouse = out_cost_warehouse;
		}

		public String getOut_non_cost_warehouse() {
			return out_non_cost_warehouse;
		}
		public void setOut_non_cost_warehouse(String out_non_cost_warehouse) {
			this.out_non_cost_warehouse = out_non_cost_warehouse;
		}

		public String getInv_cost_warehouse() {
			return inv_cost_warehouse;
		}
		public void setInv_cost_warehouse(String inv_cost_warehouse) {
			this.inv_cost_warehouse = inv_cost_warehouse;
		}

		public String getInv_non_cost_warehouse() {
			return inv_non_cost_warehouse;
		}
		public void setInv_non_cost_warehouse(String inv_non_cost_warehouse) {
			this.inv_non_cost_warehouse = inv_non_cost_warehouse;
		}

		public String getIn_cost_warehouse_name() {
			return in_cost_warehouse_name;
		}
		public void setIn_cost_warehouse_name(String in_cost_warehouse_name) {
			this.in_cost_warehouse_name = in_cost_warehouse_name;
		}

		public String getIn_non_cost_warehouse_name() {
			return in_non_cost_warehouse_name;
		}
		public void setIn_non_cost_warehouse_name(String in_non_cost_warehouse_name) {
			this.in_non_cost_warehouse_name = in_non_cost_warehouse_name;
		}

		public String getOut_cost_warehouse_name() {
			return out_cost_warehouse_name;
		}
		public void setOut_cost_warehouse_name(String out_cost_warehouse_name) {
			this.out_cost_warehouse_name = out_cost_warehouse_name;
		}

		public String getOut_non_cost_warehouse_name() {
			return out_non_cost_warehouse_name;
		}
		public void setOut_non_cost_warehouse_name(String out_non_cost_warehouse_name) {
			this.out_non_cost_warehouse_name = out_non_cost_warehouse_name;
		}

		public String getInv_cost_warehouse_name() {
			return inv_cost_warehouse_name;
		}
		public void setInv_cost_warehouse_name(String inv_cost_warehouse_name) {
			this.inv_cost_warehouse_name = inv_cost_warehouse_name;
		}

		public String getInv_non_cost_warehouse_name() {
			return inv_non_cost_warehouse_name;
		}
		public void setInv_non_cost_warehouse_name(String inv_non_cost_warehouse_name) {
			this.inv_non_cost_warehouse_name = inv_non_cost_warehouse_name;
		}
	}
}
