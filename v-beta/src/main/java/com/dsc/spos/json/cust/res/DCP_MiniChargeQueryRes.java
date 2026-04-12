package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 低消信息查询
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {

		private String miniChargeNo;
		private String mcType;
		private String adultQty;
		private String childQty;
		private String priceClean;
		private String amtMini;
		private String createBy;
		private String createByName;
		private String createDate;
		private String createTime;
		private String modifyBy;
		private String modifyByName;
		private String modifyDate;
		private String modifyTime;
		private String status;
		private String updateTime;

		private List<level2Elm> datas;


		public String getMiniChargeNo() {
			return miniChargeNo;
		}

		public void setMiniChargeNo(String miniChargeNo) {
			this.miniChargeNo = miniChargeNo;
		}

		public String getMcType() {
			return mcType;
		}

		public void setMcType(String mcType) {
			this.mcType = mcType;
		}

		public String getAdultQty() {
			return adultQty;
		}

		public void setAdultQty(String adultQty) {
			this.adultQty = adultQty;
		}

		public String getChildQty() {
			return childQty;
		}

		public void setChildQty(String childQty) {
			this.childQty = childQty;
		}

		public String getPriceClean() {
			return priceClean;
		}

		public void setPriceClean(String priceClean) {
			this.priceClean = priceClean;
		}

		public String getAmtMini() {
			return amtMini;
		}

		public void setAmtMini(String amtMini) {
			this.amtMini = amtMini;
		}

		public String getCreateBy() {
			return createBy;
		}

		public void setCreateBy(String createBy) {
			this.createBy = createBy;
		}

		public String getCreateByName() {
			return createByName;
		}

		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}

		public String getModifyByName() {
			return modifyByName;
		}

		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getModifyBy() {
			return modifyBy;
		}

		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}

		public String getModifyDate() {
			return modifyDate;
		}

		public void setModifyDate(String modifyDate) {
			this.modifyDate = modifyDate;
		}

		public String getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}

	public class level2Elm{
		private String pluNo ;
		private String pluName;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

	}



}
