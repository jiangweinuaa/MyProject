package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderPlatformMappingGoodsCreateRes  extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public class level1Elm
	{
		private String orderPluNO;
		private String orderPluName;
		private String pluBarcode;
		private String result;
		private String description;
		public String getOrderPluNO() {
			return orderPluNO;
		}
		public void setOrderPluNO(String orderPluNO) {
			this.orderPluNO = orderPluNO;
		}
		public String getOrderPluName() {
			return orderPluName;
		}
		public void setOrderPluName(String orderPluName) {
			this.orderPluName = orderPluName;
		}
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}		
		
	}
	

}
