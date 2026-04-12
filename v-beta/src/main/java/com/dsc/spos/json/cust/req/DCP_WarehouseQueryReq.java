package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 服務函數：WarehouseGet
 *    說明：仓库查询
 * 服务说明： 仓库查询
 * @author luoln
 * @since  2017-11-30
 */
public class DCP_WarehouseQueryReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
  public class levelElm{
		private String keyTxt;
		private String getType;
		private String warehouseType;
		private String shopId;

        private String isCheckRestrictGroup;
		private List<levelorgElm> orgList;

        private String[] whType;

		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getGetType() {
			return getType;
		}
		public void setGetType(String getType) {
			this.getType = getType;
		}
		public String getWarehouseType() {
			return warehouseType;
		}
		public void setWarehouseType(String warehouseType) {
			this.warehouseType = warehouseType;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

      public List<levelorgElm> getOrgList()
      {
          return orgList;
      }

      public void setOrgList(List<levelorgElm> orgList)
      {
          this.orgList = orgList;
      }

      public String[] getWhType() {
          return whType;
      }

      public void setWhType(String[] whType) {
          this.whType = whType;
      }

      public String getIsCheckRestrictGroup() {
          return isCheckRestrictGroup;
      }

      public void setIsCheckRestrictGroup(String isCheckRestrictGroup) {
          this.isCheckRestrictGroup = isCheckRestrictGroup;
      }
  }

    public class levelorgElm
    {
        private String orgNo;

        public String getOrgNo()
        {
            return orgNo;
        }

        public void setOrgNo(String orgNo)
        {
            this.orgNo = orgNo;
        }
    }


}
