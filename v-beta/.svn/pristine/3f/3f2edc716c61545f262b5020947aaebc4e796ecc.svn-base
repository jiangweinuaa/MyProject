package com.dsc.spos.json.cust.res;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：StaffShopGet
 *   說明：用户管辖门店信息查询
 * 服务说明：用户管辖门店信息查询
 * @author luoln 
 * @since  2017-03-02
 */
public class DCP_StaffShopQueryRes extends JsonRes {

	private List<level1Elm> unselectShops;
	private List<level1Elm> selectedShops;
	
	public List<level1Elm> getUnselectShops() {
		return unselectShops;
	}
	public void setUnselectShops(List<level1Elm> unselectShops) {
		this.unselectShops = unselectShops;
	}
	
	public List<level1Elm> getSelectedShops() {
		return selectedShops;
	}
	public void setSelectedShops(List<level1Elm> selectedShops) {
		this.selectedShops = selectedShops;
	}
	
	public class level1Elm
	{
		private String shopId;
		private String shopName;
		
		
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}
}
