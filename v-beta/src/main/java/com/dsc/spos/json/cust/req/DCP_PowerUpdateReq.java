package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

public class DCP_PowerUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String opGroup;
		private List<levelModular> modularPower;
		private List<levelFunction> functionPower;
		public String getOpGroup() {
			return opGroup;
		}
		public void setOpGroup(String opGroup) {
			this.opGroup = opGroup;
		}
		public List<levelModular> getModularPower() {
			return modularPower;
		}
		public void setModularPower(List<levelModular> modularPower) {
			this.modularPower = modularPower;
		}
		public List<levelFunction> getFunctionPower() {
			return functionPower;
		}
		public void setFunctionPower(List<levelFunction> functionPower) {
			this.functionPower = functionPower;
		}

	}

	public  class levelModular 
	{
		private String modularNo;
		private String powerType;

		public String getModularNo() {
			return modularNo;
		}
		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}
		public String getPowerType() {
			return powerType;
		}
		public void setPowerType(String powerType) {
			this.powerType = powerType;
		}
	}

	public  class levelFunction 
	{
		private String functionNo;
		private String powerType;

		public String getFunctionNo() {
			return functionNo;
		}
		public void setFunctionNo(String functionNo) {
			this.functionNo = functionNo;
		}
		public String getPowerType() {
			return powerType;
		}
		public void setPowerType(String powerType) {
			this.powerType = powerType;
		}
	}

}
