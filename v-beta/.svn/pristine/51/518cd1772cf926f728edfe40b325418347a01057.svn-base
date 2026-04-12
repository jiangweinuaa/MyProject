package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.cust.JsonReq;

/**
 * 赠品添加
 * @author yuanyy 2019-06-20
 *
 */
public class DCP_OrderECFreeGoodsCreateReq extends JsonReq {
	
	
	private String ecOrderNo;
	private String ecPlatformNo;
	private List<level1Elm> datas;

	public  class level1Elm{
		private String pluNo;
		private String pluName;
		private String qty;
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		
	}
	
	
	public String getEcOrderNo() {
		return ecOrderNo;
	}

	public void setEcOrderNo(String ecOrderNo) {
		this.ecOrderNo = ecOrderNo;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public String getEcPlatformNo() {
		return ecPlatformNo;
	}

	public void setEcPlatformNo(String ecPlatformNo) {
		this.ecPlatformNo = ecPlatformNo;
	}
	
}
