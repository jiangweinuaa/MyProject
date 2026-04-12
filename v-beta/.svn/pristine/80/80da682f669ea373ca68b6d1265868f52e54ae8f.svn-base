package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * 要货未到货
 * @author yuanyy
 *
 */
public class DCP_POrderNonArrivalRes extends JsonBasicRes {
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	@Data
	public class level1Elm {
		private String featureNo;
		private String pluNo;
		private String refUAQty;
		//库存数量
//		private String refWQty; //库存单位库存量
//		private String reqWQty; //要货单位库存量
		private String refBaseQty;//库存数量
		private String reqBaseQty;//要货单位库存数量
		private String preDayQty; //昨日要货量
		private String preDaySaleQty;//昨日销量（已转换为要货单位）
		private String acDemandQty;
	}
	
	
}
