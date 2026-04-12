package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

public class DCP_TouchMenuCreateReq extends JsonBasicReq {

	private String menuID;
	private String menuName;
	private String workNO;
	private String posUse;
	private String appUse;
	private String appletUse;
	private String lbDate;
	private String leDate;
	private String shopType;
	private String status;
	private String padUse;// pad导购用， Y或N
	private String priority;
	private String takeOutUse;
	private String mobileCashUse;
	private String touchMenu;

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	private List<level1Elm> datas;

	public class level1Elm {
		private String classNO;
		private String className;
		private String priority;
		private String lbTime;
		private String leTime;
		private String status;
		private String picRatio;
		private List<level2Elm> goods;

		public String getClassNO() {
			return classNO;
		}

		public void setClassNO(String classNO) {
			this.classNO = classNO;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public String getLbTime() {
			return lbTime;
		}

		public void setLbTime(String lbTime) {
			this.lbTime = lbTime;
		}

		public String getLeTime() {
			return leTime;
		}

		public void setLeTime(String leTime) {
			this.leTime = leTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getPicRatio() {
			return picRatio;
		}

		public void setPicRatio(String picRatio) {
			this.picRatio = picRatio;
		}

		public List<level2Elm> getGoods() {
			return goods;
		}

		public void setGoods(List<level2Elm> goods) {
			this.goods = goods;
		}
	}

	public class level2Elm {

		private String pluNO;
		private String dispName;
		private String unitNO;
		private String type;
		private String priority;
		private String status;

		public String getPluNO() {
			return pluNO;
		}

		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}

		public String getDispName() {
			return dispName;
		}

		public void setDispName(String dispName) {
			this.dispName = dispName;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUnitNO() {
			return unitNO;
		}

		public void setUnitNO(String unitNO) {
			this.unitNO = unitNO;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public String getMenuID() {
		return menuID;
	}

	public void setMenuID(String menuID) {
		this.menuID = menuID;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getWorkNO() {
		return workNO;
	}

	public void setWorkNO(String workNO) {
		this.workNO = workNO;
	}

	public String getPosUse() {
		return posUse;
	}

	public void setPosUse(String posUse) {
		this.posUse = posUse;
	}

	public String getAppUse() {
		return appUse;
	}

	public void setAppUse(String appUse) {
		this.appUse = appUse;
	}

	public String getAppletUse() {
		return appletUse;
	}

	public void setAppletUse(String appletUse) {
		this.appletUse = appletUse;
	}

	public String getLbDate() {
		return lbDate;
	}

	public void setLbDate(String lbDate) {
		this.lbDate = lbDate;
	}

	public String getLeDate() {
		return leDate;
	}

	public void setLeDate(String leDate) {
		this.leDate = leDate;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getPadUse() {
		return padUse;
	}

	public void setPadUse(String padUse) {
		this.padUse = padUse;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTakeOutUse() {
		return takeOutUse;
	}

	public void setTakeOutUse(String takeOutUse) {
		this.takeOutUse = takeOutUse;
	}

	public String getMobileCashUse() {
		return mobileCashUse;
	}

	public void setMobileCashUse(String mobileCashUse) {
		this.mobileCashUse = mobileCashUse;
	}

	public String getTouchMenu() {
		return touchMenu;
	}

	public void setTouchMenu(String touchMenu) {
		this.touchMenu = touchMenu;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
