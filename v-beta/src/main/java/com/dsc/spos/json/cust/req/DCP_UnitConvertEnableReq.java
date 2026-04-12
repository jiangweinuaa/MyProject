package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_UnitConvertEnableReq extends JsonBasicReq
{
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String oprType;//操作类型：1-启用2-禁用
		private	List<level1Elm> unitList;

		public String getOprType() {
			return oprType;
		}
		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		public List<level1Elm> getUnitList() {
			return unitList;
		}
		public void setUnitList(List<level1Elm> unitList) {
			this.unitList = unitList;
		}			
	}
	
	public class level1Elm
	{
		private String oUnit;
		private String unit;
		public String getUnit() {
			return unit;
		}	
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getoUnit() {
			return oUnit;
		}
		public void setoUnit(String oUnit) {
			this.oUnit = oUnit;
		}		
		
		
	}
	
}
