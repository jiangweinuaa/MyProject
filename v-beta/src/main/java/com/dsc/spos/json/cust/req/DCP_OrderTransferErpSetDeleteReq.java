package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_OrderTransferErpSetDelete
 * 服务说明：订单上传ERP白名单删除
 * @author jinzma 
 * @since  2020-12-03
 */
public class DCP_OrderTransferErpSetDeleteReq extends JsonBasicReq{
	private levelElm request;
	
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
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
		public String getShop() {
			return shop;
		}
		public void setShop(String shop) {
			this.shop = shop;
		}
	}
}
