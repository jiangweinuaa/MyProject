package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderPlatformOnshelfUpdateRes extends JsonRes  {

	private String result;
	private List<level1Elm> failures;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<level1Elm> getFailures() {
		return failures;
	}

	public void setFailures(List<level1Elm> failures) {
		this.failures = failures;
	}

	public class level1Elm
	{
		private String orderPluNO;
		private String code;
		private String description;
		public String getOrderPluNO() {
			return orderPluNO;
		}
		public void setOrderPluNO(String orderPluNO) {
			this.orderPluNO = orderPluNO;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}

	}

}
