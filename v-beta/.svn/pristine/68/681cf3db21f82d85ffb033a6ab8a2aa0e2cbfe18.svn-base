package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_CityShopQueryRes extends JsonRes
{

	private List<level1Elm> datas;

	public class level1Elm
	{
		private String CityCode;
		private String CityName;

		private List<level2Elm> children;

		public String getCityCode() {
			return CityCode;
		}

		public void setCityCode(String cityCode) {
			CityCode = cityCode;
		}

		public String getCityName() {
			return CityName;
		}

		public void setCityName(String cityName) {
			CityName = cityName;
		}

		public List<level2Elm> getChildren() {
			return children;
		}

		public void setChildren(List<level2Elm> children) {
			this.children = children;
		}

	}

	public class level2Elm
	{
		private String shopId;
		private String shopName;
		private String orgType;
        private String inCostWarehouse;
        private String outCostWarehouse;
        private String invCostWarehouse;
        private String inNonCostWarehouse;
        private String outNonCostWarehouse;
        private String invNonCostWarehouse;
        
        
		public String getInCostWarehouse() {
			return inCostWarehouse;
		}
		public void setInCostWarehouse(String inCostWarehouse) {
			this.inCostWarehouse = inCostWarehouse;
		}
		public String getOutCostWarehouse() {
			return outCostWarehouse;
		}
		public void setOutCostWarehouse(String outCostWarehouse) {
			this.outCostWarehouse = outCostWarehouse;
		}
		public String getInvCostWarehouse() {
			return invCostWarehouse;
		}
		public void setInvCostWarehouse(String invCostWarehouse) {
			this.invCostWarehouse = invCostWarehouse;
		}
		public String getInNonCostWarehouse() {
			return inNonCostWarehouse;
		}
		public void setInNonCostWarehouse(String inNonCostWarehouse) {
			this.inNonCostWarehouse = inNonCostWarehouse;
		}
		public String getOutNonCostWarehouse() {
			return outNonCostWarehouse;
		}
		public void setOutNonCostWarehouse(String outNonCostWarehouse) {
			this.outNonCostWarehouse = outNonCostWarehouse;
		}
		public String getInvNonCostWarehouse() {
			return invNonCostWarehouse;
		}
		public void setInvNonCostWarehouse(String invNonCostWarehouse) {
			this.invNonCostWarehouse = invNonCostWarehouse;
		}
		private List<WarehouseList> warehouseList;

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
		public List<WarehouseList> getWarehouseList() {
			return warehouseList;
		}
		public void setWarehouseList(List<WarehouseList> warehouseList) {
			this.warehouseList = warehouseList;
		}
		public String getOrgType() {
			return orgType;
		}
		public void setOrgType(String orgType) {
			this.orgType = orgType;
		}

	}

	public class WarehouseList{

		private String warehouse;
		private String warehouseName;
		private String warehouseType;// 1.卖场 2.仓库 3.中转仓 4.赠品仓 5.坏货仓 6.空白券库区 7.发行券库区
		
		public String getWarehouseType() {
			return warehouseType;
		}
		public void setWarehouseType(String warehouseType) {
			this.warehouseType = warehouseType;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}

	}

	public List<level1Elm> getDatas() {
		return datas;
	}



	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
