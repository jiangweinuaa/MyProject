package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PosPageFuncQueryRes extends JsonRes
{

	private List<level1Elm> datas;
	
	
	public class level1Elm
	{				
		private String funcNo;
		private String funcName;	
		private String sortId;//显示顺序，排序第一列
		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getSortId()
		{
			return sortId;
		}
		public void setSortId(String sortId)
		{
			this.sortId = sortId;
		}
		
			
		
	}


	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
	
	
	
}
