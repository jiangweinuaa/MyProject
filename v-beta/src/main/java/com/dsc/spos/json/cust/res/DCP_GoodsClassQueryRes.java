package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_GoodsClassQueryRes extends JsonBasicRes  {

private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String classNo;
		private String className;
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
			
		
	}
}
