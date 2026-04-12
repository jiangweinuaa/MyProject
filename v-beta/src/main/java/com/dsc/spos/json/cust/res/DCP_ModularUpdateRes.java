package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;
public class DCP_ModularUpdateRes extends JsonBasicRes
{

	private List<level2Elm> datas;
	
	public class level2Elm
	{
		private String funcNO;

	public String getFuncNO() {
		return funcNO;
	}

	public void setFuncNO(String funcNO) {
		this.funcNO = funcNO;
	}
		
	}

	
	public List<level2Elm> getDatas() {
	return datas;
	}

	public void setDatas(List<level2Elm> datas) {
	this.datas = datas;
	}
	
}
