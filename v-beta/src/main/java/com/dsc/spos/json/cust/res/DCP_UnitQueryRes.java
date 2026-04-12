package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * UnitGetRes单位查询 Response JSON
 * @author y
 *
 */
public class DCP_UnitQueryRes extends JsonRes
{
	/**
  	* {
	  	"success": true, 成功否
	  	"serviceStatus": "000",服務狀態代碼
	  	"serviceDescription": "服務執行成功",服務狀態說明
		datas: [	
		   	{ 	
		      "unitNO": "001",	单位编码
		      "unitName": "个",	单位名称
			}	
		 ]	
	* }      
	**/	
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}	
	
	public class level1Elm
	{		
		private String unit;
		private String unitName;
		private String wunit;
		private String unit_ratio;
		private String unitUdLength;
		
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		
		public String getWunit() {
			return wunit;
		}
		public void setWunit(String wunit) {
			this.wunit = wunit;
		}
		
		public String getUnit_ratio() {
			return unit_ratio;
		}
		public void setUnit_ratio(String unit_ratio) {
			this.unit_ratio = unit_ratio;
		}
		public String getUnitUdLength() {
			return unitUdLength;
		}
		public void setUnitUdLength(String unitUdLength) {
			this.unitUdLength = unitUdLength;
		}
		
		
		
	}		
}
