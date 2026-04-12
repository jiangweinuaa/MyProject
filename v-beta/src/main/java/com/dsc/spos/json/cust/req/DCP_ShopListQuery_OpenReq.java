package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 门店列表查询
 * @author Huawei
 *
 */
public class DCP_ShopListQuery_OpenReq extends JsonBasicReq {
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

//	public String getTimestamp() {
//		return timestamp;
//	}
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm{
		private String eId;
		private String longitude;
		private String latitude;

		private String city;
		private String keyTxt; // 门店名称/地址模糊查询
		private String shopId; // 门店编号

        private String companyId;   // 所属公司ID
		private String pageSize;
		private String pageNumber;


        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getLongitude() {
			return longitude;
		}

		public String getLatitude() {
			return latitude;
		}

		public String getCity() {
			return city;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public void setCity(String city) {
			this.city = city;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getPageSize() {
			return pageSize;
		}
		public String getPageNumber() {
			return pageNumber;
		}

		public void setPageSize(String pageSize) {
			this.pageSize = pageSize;
		}

		public void setPageNumber(String pageNumber) {
			this.pageNumber = pageNumber;
		}

        public String getKeyTxt() {
            return keyTxt;
        }

        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }
    }
}
