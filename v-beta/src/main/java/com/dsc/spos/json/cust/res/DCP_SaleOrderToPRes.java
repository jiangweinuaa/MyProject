package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_SaleOrderToPRes extends JsonRes {
	private List<level2Elm> datas;

	public List<level2Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level2Elm> datas) {
		this.datas = datas;
	}

	public class level2Elm {
		private String porderNO;

		public String getPorderNO() {
			return porderNO;
		}

		public void setPorderNO(String porderNO) {
			this.porderNO = porderNO;
		}

	}
}
