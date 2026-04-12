package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderDeliveryProcessRes extends JsonRes {

	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String orderNO;
		private String requestDeliveryNO;//创建时我们生成的物流单号
		private String responseDeliveryNO;//下单成功后返回的物流单号	
		private String result;// Y-成功，N-失败
		private String description;
		public String getOrderNO() {
			return orderNO;
		}
		public void setOrderNO(String orderNO) {
			this.orderNO = orderNO;
		}
		public String getRequestDeliveryNO() {
			return requestDeliveryNO;
		}
		public void setRequestDeliveryNO(String requestDeliveryNO) {
			this.requestDeliveryNO = requestDeliveryNO;
		}
		public String getResponseDeliveryNO() {
			return responseDeliveryNO;
		}
		public void setResponseDeliveryNO(String responseDeliveryNO) {
			this.responseDeliveryNO = responseDeliveryNO;
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
