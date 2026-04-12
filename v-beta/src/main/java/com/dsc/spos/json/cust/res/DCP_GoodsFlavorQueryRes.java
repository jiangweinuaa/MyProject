package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 商品口味查询 2018-09-20	
 * @author yuanyy
 *
 */

public class DCP_GoodsFlavorQueryRes extends JsonRes {

	private List<level1Elm> datas ;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String flavorNo;
		private String flavorName;
		private String priority;
		private String status;

		public String getFlavorNo() {
			return flavorNo;
		}
		public void setFlavorNo(String flavorNo) {
			this.flavorNo = flavorNo;
		}
		public String getFlavorName() {
			return flavorName;
		}
		public void setFlavorName(String flavorName) {
			this.flavorName = flavorName;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}



	}

}
