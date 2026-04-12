package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 同期五周营业额查询
 * @author yuanyy  2019-08-13
 *
 */
public class DCP_SaleAmtOldQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm {
		
		private String eDate;
		private String saleAmt;
		public String geteDate() {
			return eDate;
		}
		public void seteDate(String eDate) {
			this.eDate = eDate;
		}
		public String getSaleAmt() {
			return saleAmt;
		}
		public void setSaleAmt(String saleAmt) {
			this.saleAmt = saleAmt;
		}
		
	}
	
}
