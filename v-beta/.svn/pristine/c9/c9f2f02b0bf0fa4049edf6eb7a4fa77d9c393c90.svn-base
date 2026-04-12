package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：RejectCreateDCP
 * 服务说明：门店单据驳回
 * @author jinzma 
 * @since  2019-05-29
 */
public class DCP_RejectCreateReq extends JsonBasicReq {

	private String docType;
	private List<level1Elm> datas;

	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public  class level1Elm
	{
		private String shopId;
		private String docNO;
		private String reason;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getDocNO() {
			return docNO;
		}
		public void setDocNO(String docNO) {
			this.docNO = docNO;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}

	}
}
