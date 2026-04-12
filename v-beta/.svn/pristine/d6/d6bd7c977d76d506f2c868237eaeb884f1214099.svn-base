package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_GoodsBomQuery
 * 服务说明：商品BOM查询
 * @author jinzma
 * @since 2020-08-14
 */
public class DCP_GoodsBomQueryReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String bomType;
		private String shopId;

        private String isCheckMaterialStock;
        private String warehouse;
		private List<level1Elm> pluList;
		
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getBomType() {
			return bomType;
		}
		public void setBomType(String bomType) {
			this.bomType = bomType;
		}
		public List<level1Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level1Elm> pluList) {
			this.pluList = pluList;
		}

        public String getIsCheckMaterialStock() {
            return isCheckMaterialStock;
        }

        public void setIsCheckMaterialStock(String isCheckMaterialStock) {
            this.isCheckMaterialStock = isCheckMaterialStock;
        }

        public String getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }
    }
	
	public class level1Elm{
		private String pluNo;
		private String prodUnit;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getProdUnit() {
			return prodUnit;
		}
		public void setProdUnit(String prodUnit) {
			this.prodUnit = prodUnit;
		}
	}
}
