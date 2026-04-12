package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.JsonBasicRes;

/**
 * StockInCreate 專用的 response json
 * @author panjing 
 * @since  2016-10-09
 */

public class DCP_StockInCreateRes extends JsonBasicRes {
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String stockInNo;

		public String getStockInNo() {
			return stockInNo;
		}

		public void setStockInNo(String stockInNo) {
			this.stockInNo = stockInNo;
		}

	}
}