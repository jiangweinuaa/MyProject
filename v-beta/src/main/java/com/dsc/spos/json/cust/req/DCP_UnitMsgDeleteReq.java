package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 删除单位信息 2018-09-20
 * @author yuanyy
 *
 */
public class DCP_UnitMsgDeleteReq extends JsonBasicReq {
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private List<level1Elm> unitList;

		public List<level1Elm> getUnitList() {
			return unitList;
		}
	
		public void setUnitList(List<level1Elm> unitList) {
			this.unitList = unitList;
		}
		
	}
	public class level1Elm
	{
		private String unit;

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		} 
		
	}
	
}
