package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * POS班次记录查询
 * @author yuanyy 2019-05-20
 *
 */
public class DCP_SquadQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm {
		private String shopId;
		private String shopName;
		private String bDate;
		private String squadNo;
		private String machine;
		private String machineName;
		private String offOPNo;
		private String offOPName;
		private String workNo;
		private String workName;
		private String tc_name;
		private String endSquad;

		// 以下几个字段也许会用到，也给查出来
		private String tc_cash;
		private String PDA_cash;
		private String saleCash;
		private String extraCash;
		private String pettyCash;
		private String largeCash;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getOffOPNo() {
			return offOPNo;
		}
		public void setOffOPNo(String offOPNo) {
			this.offOPNo = offOPNo;
		}
		public String getOffOPName() {
			return offOPName;
		}
		public void setOffOPName(String offOPName) {
			this.offOPName = offOPName;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getSquadNo() {
			return squadNo;
		}
		public void setSquadNo(String squadNo) {
			this.squadNo = squadNo;
		}
		public String getMachine() {
			return machine;
		}
		public void setMachine(String machine) {
			this.machine = machine;
		}
		public String getMachineName() {
			return machineName;
		}
		public void setMachineName(String machineName) {
			this.machineName = machineName;
		}
		public String getWorkNo() {
			return workNo;
		}
		public void setWorkNo(String workNo) {
			this.workNo = workNo;
		}
		public String getWorkName() {
			return workName;
		}
		public void setWorkName(String workName) {
			this.workName = workName;
		}
		public String getTc_name() {
			return tc_name;
		}
		public void setTc_name(String tc_name) {
			this.tc_name = tc_name;
		}
		public String getEndSquad() {
			return endSquad;
		}
		public void setEndSquad(String endSquad) {
			this.endSquad = endSquad;
		}
		public String getTc_cash() {
			return tc_cash;
		}
		public void setTc_cash(String tc_cash) {
			this.tc_cash = tc_cash;
		}
		public String getPDA_cash() {
			return PDA_cash;
		}
		public void setPDA_cash(String pDA_cash) {
			PDA_cash = pDA_cash;
		}
		public String getSaleCash() {
			return saleCash;
		}
		public void setSaleCash(String saleCash) {
			this.saleCash = saleCash;
		}
		public String getExtraCash() {
			return extraCash;
		}
		public void setExtraCash(String extraCash) {
			this.extraCash = extraCash;
		}
		public String getPettyCash() {
			return pettyCash;
		}
		public void setPettyCash(String pettyCash) {
			this.pettyCash = pettyCash;
		}
		public String getLargeCash() {
			return largeCash;
		}
		public void setLargeCash(String largeCash) {
			this.largeCash = largeCash;
		}


	}
}
