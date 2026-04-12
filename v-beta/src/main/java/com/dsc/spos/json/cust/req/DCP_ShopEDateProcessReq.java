package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：ShopEDateProcessDCP
 * 服务说明：日结处理DCP
 * @author jinzma 
 * @since  2019-05-10
 */
public class DCP_ShopEDateProcessReq extends JsonBasicReq  {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String eType ;
		private List<level1Elm> datas;
		public String geteType() {
			return eType;
		}
		public void seteType(String eType) {
			this.eType = eType;
		}
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	public  class level1Elm
	{
		private String oEId;
		private String oShopId ;
		private String eDate ;

		public String getoShopId() {
			return oShopId;
		}
		public void setoShopId(String oShopId) {
			this.oShopId = oShopId;
		}
		public String geteDate() {
			return eDate;
		}
		public void seteDate(String eDate) {
			this.eDate = eDate;
		}
		public String getoEId() {
			return oEId;
		}
		public void setoEId(String oEId) {
			this.oEId = oEId;
		}
	}
}
