package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色查询 2018-09-26	
 * @author yuanyy
 *
 */
public class DCP_RoleQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm 
	{

		private String opGroup;
		private String opgName;
		private int disc;
		private String isMask;
		private String isShowDistriPrice;
		private String status;
		private String maxFreeAmt;

        private String role;


		private List<ModularPower> modularPower;
		private List<FunctionPower> functionPower;
		private List<User> userList;
		private String userCount;
		private String creatorID;
		private String creatorName;
		private String creatorDeptID;
		private String creatorDeptName;
		private String createDatetime;
		private String lastmodifyID;
		private String lastmodifyName;
		private String lastmodifyDatetime;

		//private List<DCP_RoleQueryRes.level1Elm> children;


		public List<ModularPower> getModularPower() {
			return modularPower;
		}

		public void setModularPower(List<ModularPower> modularPower) {
			this.modularPower = modularPower;
		}


		public List<FunctionPower> getFunctionPower() {
			return functionPower;
		}

		public void setFunctionPower(List<FunctionPower> functionPower) {
			this.functionPower = functionPower;
		}
		public List<User> getUserList() {
			return userList;
		}

		public void setUserList(List<User> userList) {
			this.userList = userList;
		}
		public String getUserCount() {
			return userCount;
		}

		public void setUserCount(String userCount) {
			this.userCount = userCount;
		}

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
		public int getDisc() {
			return disc;
		}
		public void setDisc(int disc) {
			this.disc = disc;
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

		public String getCreatorID() {
			return creatorID;
		}

		public void setCreatorID(String creatorID) {
			this.creatorID = creatorID;
		}

		public String getCreatorName() {
			return creatorName;
		}

		public void setCreatorName(String creatorName) {
			this.creatorName = creatorName;
		}

		public String getCreatorDeptID() {
			return creatorDeptID;
		}

		public void setCreatorDeptID(String creatorDeptID) {
			this.creatorDeptID = creatorDeptID;
		}

		public String getCreatorDeptName() {
			return creatorDeptName;
		}

		public void setCreatorDeptName(String creatorDeptName) {
			this.creatorDeptName = creatorDeptName;
		}

		public String getCreateDatetime() {
			return createDatetime;
		}

		public void setCreateDatetime(String createDatetime) {
			this.createDatetime = createDatetime;
		}

		public String getLastmodifyID() {
			return lastmodifyID;
		}

		public void setLastmodifyID(String lastmodifyID) {
			this.lastmodifyID = lastmodifyID;
		}

		public String getLastmodifyName() {
			return lastmodifyName;
		}

		public void setLastmodifyName(String lastmodifyName) {
			this.lastmodifyName = lastmodifyName;
		}

		public String getLastmodifyDatetime() {
			return lastmodifyDatetime;
		}

		public void setLastmodifyDatetime(String lastmodifyDatetime) {
			this.lastmodifyDatetime = lastmodifyDatetime;
		}

		//public List<level1Elm> getChildren() {
		//	return children;
		//}

		//public void setChildren(List<level1Elm> children) {
		//	this.children = children;
		//}
	}

	@Getter
	@Setter
	public class ModularPower{
		private String modularNo;
		private String powerType;
		private String queryRange;
		private String editRange;
		private String deleteRange;
	}

	@Getter
	@Setter
	public class  FunctionPower{

		private String functionNo;

		private String powerType;

	}

	@Getter
	@Setter
	public  class User{
		private String opNo;

		private String opName;

		private String status;
	}
}

