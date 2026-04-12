package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 调拨模板查询  DCP_StockOutTemplateGetRes
 * @author yuanyy
 *
 */
public class DCP_StockOutTemplateDetailRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String item;
		private String pluNo;
		private String pluName;
		private String punit;
		private String punitName;
		private String status;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
		
		public String getPunit()
		{
			return punit;
		}
		public void setPunit(String punit)
		{
			this.punit = punit;
		}
		public String getPunitName()
		{
			return punitName;
		}
		public void setPunitName(String punitName)
		{
			this.punitName = punitName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}


}