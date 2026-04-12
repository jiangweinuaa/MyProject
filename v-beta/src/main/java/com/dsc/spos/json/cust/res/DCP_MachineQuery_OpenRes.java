package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 机台查询，PAD 导购用
 * @author yuany
 *
 */
public class DCP_MachineQuery_OpenRes extends JsonBasicRes {
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String eId;
		private List<level2Elm> shopList;
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public List<level2Elm> getShopList() {
			return shopList;
		}
		public void setShopList(List<level2Elm> shopList) {
			this.shopList = shopList;
		}

	}
	
	public class level2Elm{
		
		private String shopId;
		private String shopName;
		private String orgForm;
		
		private List<level3Elm> machineList;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getShopName() {
			return shopName;
		}

		public void setShopName(String shopName) {
			this.shopName = shopName;
		}

		public String getOrgForm() {
			return orgForm;
		}

		public void setOrgForm(String orgForm) {
			this.orgForm = orgForm;
		}

		public List<level3Elm> getMachineList() {
			return machineList;
		}

		public void setMachineList(List<level3Elm> machineList) {
			this.machineList = machineList;
		}
		
	}
	
	public class level3Elm{
		
		private String machineId;
		private String machineName;
//		private String machineCode;
		
		public String getMachineId() {
			return machineId;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public String getMachineName() {
			return machineName;
		}
		public void setMachineName(String machineName) {
			this.machineName = machineName;
		}
//		public String getMachineCode() {
//			return machineCode;
//		}
//		public void setMachineCode(String machineCode) {
//			this.machineCode = machineCode;
//		}
//		
	}
	
	
	
}
