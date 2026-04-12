package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_MultiSpecGoodsQueryRes extends JsonRes {
	
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() 
	{
		return datas;
	}
	public void setDatas(List<level1Elm> datas)
	{
		this.datas = datas;
	}

	public class level1Elm
	{
		private String masterPluNo;
		private String masterPluName;
		private String memo;	
		private String status;
		private String minPrice;//最小价格	
		private String maxPrice;//最大价格
		public String getMasterPluNo() {
			return masterPluNo;
		}
		public void setMasterPluNo(String masterPluNo) {
			this.masterPluNo = masterPluNo;
		}
		public String getMasterPluName() {	
			return masterPluName;
		}
		public void setMasterPluName(String masterPluName) {
			this.masterPluName = masterPluName;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getMinPrice() {
			return minPrice;
		}
		public void setMinPrice(String minPrice) {
			this.minPrice = minPrice;
		}
		public String getMaxPrice() {
			return maxPrice;
		}
		public void setMaxPrice(String maxPrice) {
			this.maxPrice = maxPrice;
		}
				
		
	}

}
