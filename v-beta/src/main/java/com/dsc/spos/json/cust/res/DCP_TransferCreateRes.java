package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * TransferCreate 專用的 response json
 * @author luoln
 * @since  2017-12-15
 */
public class DCP_TransferCreateRes extends JsonBasicRes {
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm {
	}
}
