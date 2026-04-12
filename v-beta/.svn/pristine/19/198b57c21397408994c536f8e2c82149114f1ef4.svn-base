package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 门店列表查询
 * @author Huawei
 *
 */
public class DCP_ShopListQuery_OpenRes extends JsonRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{

		private String shopId;
		private String shopName;
		private String fileName; //完整路径 
		private String province;
		private String city;
		private String area;
		private String address;
		private String longitude;
		private String latitude;
		private String phone;
		private String grade;
		private String distance;
		private String openHours;

		private level3Elm delivery; // 外卖信息
		private String scanType; // 点餐模式 0.正餐(后结) 1.快餐/街饮(先结)
		private String restrictTable; // 是否启用桌台 0.否 1.是
		private String restrictAdvanceOrder; // 提前选菜0.禁用 1.启用

		public String getShopName() {
			return shopName;
		}
		public String getProvince() {
			return province;
		}
		public String getCity() {
			return city;
		}
		public String getArea() {
			return area;
		}
		public String getAddress() {
			return address;
		}
		public String getLongitude() {
			return longitude;
		}
		public String getLatitude() {
			return latitude;
		}
		public String getPhone() {
			return phone;
		}
		public String getGrade() {
			return grade;
		}
		public String getDistance() {
			return distance;
		}
		public String getOpenHours() {
			return openHours;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public void setGrade(String grade) {
			this.grade = grade;
		}
		public void setDistance(String distance) {
			this.distance = distance;
		}
		public void setOpenHours(String openHours) {
			this.openHours = openHours;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public level3Elm getDelivery() {
			return delivery;
		}

		public void setDelivery(level3Elm delivery) {
			this.delivery = delivery;
		}

		public String getScanType() {
			return scanType;
		}

		public void setScanType(String scanType) {
			this.scanType = scanType;
		}

		public String getRestrictAdvanceOrder() {
			return restrictAdvanceOrder;
		}

		public void setRestrictAdvanceOrder(String restrictAdvanceOrder) {
			this.restrictAdvanceOrder = restrictAdvanceOrder;
		}

		public String getRestrictTable() {
			return restrictTable;
		}

		public void setRestrictTable(String restrictTable) {
			this.restrictTable = restrictTable;
		}
	}

	public class level2Elm{

		private String item;
		private String startTime;
		private String endTime;

		public String getItem() {
			return item;
		}
		public String getStartTime() {
			return startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

	}

	public class level3Elm{
	    private String isSelfPick; // 支持自提（Y，N）
        private String selfBeginTime; // 自提开始时间
        private String selfEndTime; // 自提截止时间
        private String isCityDelivery; // 支持同城配送（Y，N）
        private List<level2Elm> orderTimes; // 配送接单时间
        private String isAddressDelivery; // 支持当前地址配送(Y/N)

        public String getIsSelfPick() {
            return isSelfPick;
        }

        public void setIsSelfPick(String isSelfPick) {
            this.isSelfPick = isSelfPick;
        }

        public String getSelfBeginTime() {
            return selfBeginTime;
        }

        public void setSelfBeginTime(String selfBeginTime) {
            this.selfBeginTime = selfBeginTime;
        }

        public String getSelfEndTime() {
            return selfEndTime;
        }

        public void setSelfEndTime(String selfEndTime) {
            this.selfEndTime = selfEndTime;
        }

        public String getIsCityDelivery() {
            return isCityDelivery;
        }

        public void setIsCityDelivery(String isCityDelivery) {
            this.isCityDelivery = isCityDelivery;
        }

        public List<level2Elm> getOrderTimes() {
            return orderTimes;
        }

        public void setOrderTimes(List<level2Elm> orderTimes) {
            this.orderTimes = orderTimes;
        }

        public String getIsAddressDelivery() {
            return isAddressDelivery;
        }

        public void setIsAddressDelivery(String isAddressDelivery) {
            this.isAddressDelivery = isAddressDelivery;
        }

	}


}
