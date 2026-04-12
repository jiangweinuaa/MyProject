package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：UndoCreateDCP
 * 服务说明：门店单据撤销
 * @author jinzma 
 * @since  2019-06-11
 */
public class DCP_UndoCreateReq  extends JsonBasicReq {

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
		private String loadDocNO;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getLoadDocNO() {
			return loadDocNO;
		}
		public void setLoadDocNO(String loadDocNO) {
			this.loadDocNO = loadDocNO;
		}

	}

}
