package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_RoleCreateReq extends JsonBasicReq {
	
	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String opGroup;
		private String opgName;
		private String disc;
		private String status;
		private String roleID;
		private String isMask;
		private String isShowDistriPrice;
		private String maxFreeAmt;

        private List<ModularPower> modularPower;
        public void setModularPower(List<ModularPower> modularPower) {
			this.modularPower = modularPower;
		}

        public List<ModularPower> getModularPower() {
			return modularPower;
		}

        private List<FunctionPower> functionPower;
        public void setFunctionPower(List<FunctionPower> functionPower) {
			this.functionPower = functionPower;
		}
        public  List<FunctionPower> getFunctionPower() {
			return functionPower;
		}

        private String role;
        public String getRole() {
			return role;
		}
        public void setRole(String role) {
            this.role = role;
        }


		public String getOpGroup() {
			return opGroup;
		}
		public void setOpGroup(String opGroup) {
			this.opGroup = opGroup;
		}
		public String getOpgName() {
			return opgName;
		}
		public void setOpgName(String opgName) {
			this.opgName = opgName;
		}
		public String getDisc() {
			return disc;
		}
		public void setDisc(String disc) {
			this.disc = disc;
		}
		public String getRoleID() {
			return roleID;
		}
		public void setRoleID(String roleID) {
			this.roleID = roleID;
		}
		public String getIsMask() {
			return isMask;
		}
		public void setIsMask(String isMask) {
			this.isMask = isMask;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getIsShowDistriPrice() {
			return isShowDistriPrice;
		}
		public void setIsShowDistriPrice(String isShowDistriPrice) {
			this.isShowDistriPrice = isShowDistriPrice;
		}
		public String getMaxFreeAmt() {
			return maxFreeAmt;
		}
		public void setMaxFreeAmt(String maxFreeAmt) {
			this.maxFreeAmt = maxFreeAmt;
		}

	}

    @Getter
    @Setter
    public class  ModularPower{

        public String modularNo;
        public String powerType;
        public String queryRange;
        public String editRange;
        public String deleteRange;
    }

    @Getter
    @Setter
    public class FunctionPower{
        public String functionNo;
        public String powerType;

    }


	
	

}
