package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 可用性库存数量查询
 * @author 2020-06-17
 *
 */
public class DCP_StockQtyQueryRes extends JsonRes {

	private levelRes datas;

	public levelRes getDatas() {
		return datas;
	}

	public void setDatas(levelRes datas) {
		this.datas = datas;
	}

	public class levelRes{
		
		private List<PluList> pluList;

		public List<PluList> getPluList() {
			return pluList;
		}

		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	public class PluList{
		
		private String pluNo;
		private String sUnit;
		private String featureNo;
		private String qty;
		
		public String getPluNo() {
			return pluNo;
		}
		public String getsUnit() {
			return sUnit;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public String getQty() {
			return qty;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		
	}

}
