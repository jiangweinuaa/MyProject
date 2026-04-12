package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

public class DCP_POrderOTUsageRes extends JsonBasicRes {
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	@Data
	public class level1Elm {
		private String pluNo;
		private String featureNo;
		private String refBaseQty;
		private String refPqty;
		private String propQty;        // 预估量(要货单位计算)
		
		//2019-02-20 yyy 增加三个字段： 千元量、 千元调整量  、 预估调整量
		private String kQty;           // 千元量(库存单位计算)
		private String kAdjQty;        //千元调整量 (库存单位计算)
		private String propAdjQty;     //预估调整量
	}
	
	
	
}
