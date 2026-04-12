package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_OrderTransferErpSetQuery
 * 服务说明：订单上传ERP白名单查询
 * @author jinzma 
 * @since  2020-12-03
 */
public class DCP_OrderTransferErpSetQueryRes extends JsonRes{
	private levelElm datas;

	public levelElm getDatas() {
		return datas;
	}
	public void setDatas(levelElm datas) {
		this.datas = datas;
	}
	public class levelElm{
		private List<level1Elm> orgList;

		public List<level1Elm> getOrgList() {
			return orgList;
		}
		public void setOrgList(List<level1Elm> orgList) {
			this.orgList = orgList;
		}
	}
	public class level1Elm{
		private String shop;
		private String shopName;
		private String createOpId;
		private String createOpName;
		private String createTime;

		public String getShop() {
			return shop;
		}
		public void setShop(String shop) {
			this.shop = shop;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getCreateOpId() {
			return createOpId;
		}
		public void setCreateOpId(String createOpId) {
			this.createOpId = createOpId;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}		
	}
}
