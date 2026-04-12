package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 单位信息查询 2018-09-20
 * @author yuanyy	
 *
 */
public class DCP_UnitConvertQueryRes extends JsonRes {

	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String oUnit;
		private String unit;
		private String unitRatio;
		private String status;
		private String oUnitName;
		private String unitName;
		
		private String createId; //add by 01029 20240703
		private String createIdName;
		private String createDep;
		private String createDepName;
		
		private String createTime;
		private String modiId; 
		private String modiIdName; 
		private String modiTime; 
		public String getoUnit() {
			return oUnit;
		}
		public void setoUnit(String oUnit) {
			this.oUnit = oUnit;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getoUnitName() {
			return oUnitName;
		}
		public void setoUnitName(String oUnitName) {
			this.oUnitName = oUnitName;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getCreateId() {
			return createId;
		}
		public void setCreateId(String createId) {
			this.createId = createId;
		}
		public String getCreateDep() {
			return createDep;
		}
		public String getCreateIdName() {
			return createIdName;
		}
		public void setCreateIdName(String createIdName) {
			this.createIdName = createIdName;
		}
		public String getCreateDepName() {
			return createDepName;
		}
		public void setCreateDepName(String createDepName) {
			this.createDepName = createDepName;
		}
		public String getModiIdName() {
			return modiIdName;
		}
		public void setModiIdName(String modiIdName) {
			this.modiIdName = modiIdName;
		}
		
		public void setCreateDep(String createDep) {
			this.createDep = createDep;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getModiId() {
			return modiId;
		}
		public void setModiId(String modiId) {
			this.modiId = modiId;
		}
		public String getModiTime() {
			return modiTime;
		}
		public void setModiTime(String modiTime) {
			this.modiTime = modiTime;
		}
	
			
	}
	
	
	
	
	
}
