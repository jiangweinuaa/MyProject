package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 单位信息新增 2018-09-20	
 * @author yuanyy
 *
 */
public class DCP_UnitMsgUpdateReq extends JsonBasicReq{
	
private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String unit;	
		private String udLength;
		private String status;

    private int unitType;//单位类型：1-数量单位、2-体积单位、3-面积单位、4-长度单位、5-重量单位
    private int roundType;//1-四舍五入、2-四舍六入五成双、3-无条件舍弃、4-无条件进位
		
		private List<level1Elm> unitName_lang;

		public String getUnit() {
			return unit;
		}
	
		public void setUnit(String unit) {
			this.unit = unit;
		}
	
	
		public String getUdLength() {
			return udLength;
		}
	
		public void setUdLength(String udLength) {
			this.udLength = udLength;
		}
	
		public String getStatus() {
			return status;
		}
	
		public void setStatus(String status) {
			this.status = status;
		}

    public int getUnitType() {
      return unitType;
    }

    public void setUnitType(int unitType) {
      this.unitType = unitType;
    }

    public int getRoundType() {
      return roundType;
    }

    public void setRoundType(int roundType) {
      this.roundType = roundType;
    }

    public List<level1Elm> getUnitName_lang() {
			return unitName_lang;
		}
	
		public void setUnitName_lang(List<level1Elm> unitName_lang) {
			this.unitName_lang = unitName_lang;
		}
	
			
			
		
	}
	

	
	
	public  class level1Elm
	{
		private String name;
		private String langType;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}		
		
	}
	
}
