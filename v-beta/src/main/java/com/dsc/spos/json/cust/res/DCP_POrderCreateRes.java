package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.JsonBasicRes;

/**
 * POrderCreate 專用的 response json
 *   說明：要货单保存
 * 服务说明：要货单保存
 * @author panjing 
 * @since  2016-10-08
 */ 

public class DCP_POrderCreateRes extends JsonBasicRes {
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public class level1Elm
	{
		private String porderNo;
		public String getPorderNo() {
			return porderNo;
		}
		public void setPorderNo(String porderNo) {
			this.porderNo = porderNo;
		}


	}
}