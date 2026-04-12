package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 预估要货量查询  （通过盘点 来预估报货数量）
 * @author yuanyy
 *
 */
public class DCP_POrderPropQtyQueryReq extends JsonBasicReq {
	
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public  class level1Elm{
		private String pluNO;
		private String punit;

		public String getPluNO() {
			return pluNO;
		}

		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}

		public String getPunit() {
			return punit;
		}

		public void setPunit(String punit) {
			this.punit = punit;
		}
		
	}
}
