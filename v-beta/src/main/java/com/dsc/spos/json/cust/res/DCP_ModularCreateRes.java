package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_ModularCreateRes extends JsonBasicRes
{

	private List<level2Elm> datas;
	
	
	public List<level2Elm> getDatas() {
		return datas;
		}

		public void setDatas(List<level2Elm> datas) {
		this.datas = datas;
		}
	
		
	public class level2Elm
	{
		
		private String MODULARNO;

		public String getMODULARNO() 
		{
			return MODULARNO;
		}

		public void setMODULARNO(String MODULARNO) 
		{
			this.MODULARNO = MODULARNO;
		}
		
	}

	
	
	
	
}
