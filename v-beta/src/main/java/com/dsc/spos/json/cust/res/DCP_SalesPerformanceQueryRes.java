package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 销售业绩查询
 * @author yuanyy
 *
 */
public class DCP_SalesPerformanceQueryRes extends JsonRes {
	private String totOQty;//订单总数
	private String totAmt; // 交易总金额
	
	private List<level1Elm> datas;

	public String getTotOQty() {
		return totOQty;
	}

	public void setTotOQty(String totOQty) {
		this.totOQty = totOQty;
	}

	public String getTotAmt() {
		return totAmt;
	}

	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String saleNo;
		private String payStatus;
		private String saleAmt;
		private String saleDate;
		private String saleType;
		public String getSaleNo() {
			return saleNo;
		}
		public void setSaleNo(String saleNo) {
			this.saleNo = saleNo;
		}
		public String getPayStatus() {
			return payStatus;
		}
		public void setPayStatus(String payStatus) {
			this.payStatus = payStatus;
		}
		public String getSaleAmt() {
			return saleAmt;
		}
		public void setSaleAmt(String saleAmt) {
			this.saleAmt = saleAmt;
		}
		public String getSaleDate() {
			return saleDate;
		}
		public void setSaleDate(String saleDate) {
			this.saleDate = saleDate;
		}
		public String getSaleType() {
			return saleType;
		}
		public void setSaleType(String saleType) {
			this.saleType = saleType;
		}
		
	}
	
}
