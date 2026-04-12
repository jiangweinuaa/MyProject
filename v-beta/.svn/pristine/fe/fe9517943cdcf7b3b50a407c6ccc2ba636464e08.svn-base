package com.dsc.spos.json.cust.res;
import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_ParaSetQueryRes.level1Elm;

/**
 * 服務函數：DiscGetDCP
 *   說明：触屏折扣查询DCP
 * 服务说明：触屏折扣查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DiscQueryRes extends JsonRes {

	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String keyValue;
		private String priority;
		private String status;
		private List<level2Elm> shops;

		public String getKeyValue() {
			return keyValue;
		}
		public void setKeyValue(String keyValue) {
			this.keyValue = keyValue;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public List<level2Elm> getShops() {
			return shops;
		}
		public void setShops(List<level2Elm> shops) {
			this.shops = shops;
		}
	}

	public class level2Elm{
		private String shopId;
		private String shopName;

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


	}




}
