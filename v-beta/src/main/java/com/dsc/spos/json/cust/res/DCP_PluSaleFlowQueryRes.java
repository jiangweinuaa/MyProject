package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 商品销售流水查询
 * @author yuanyy
 *	
 */
public class DCP_PluSaleFlowQueryRes extends JsonRes{
	private level1Elm datas;

	public level1Elm getDatas() {
		return datas;
	}

	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String pluNo;
		private String pUnit;
		private String pluName;
		private String guQingNo;
		private List<level2Elm> data;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getGuQingNo() {
			return guQingNo;
		}
		public void setGuQingNo(String guQingNo) {
			this.guQingNo = guQingNo;
		}
		public List<level2Elm> getData() {
			return data;
		}
		public void setData(List<level2Elm> data) {
			this.data = data;
		}
	
	}
	
	public class level2Elm{
		private String dtNo;
		private String dtName;
		private String beginTime;
		private String endTime;
		private String item;
		private String modify_date;
		private String modify_time;
		private String qty;
		private String saleNo;
		private String platformType; //pos：POS；app：APP云POS；saoMa：扫码点餐
		public String getDtNo() {
			return dtNo;
		}
		public void setDtNo(String dtNo) {
			this.dtNo = dtNo;
		}
		public String getDtName() {
			return dtName;
		}
		public void setDtName(String dtName) {
			this.dtName = dtName;
		}
		public String getBeginTime() {
			return beginTime;
		}
		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getModify_date() {
			return modify_date;
		}
		public void setModify_date(String modify_date) {
			this.modify_date = modify_date;
		}
		public String getModify_time() {
			return modify_time;
		}
		public void setModify_time(String modify_time) {
			this.modify_time = modify_time;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getSaleNo() {
			return saleNo;
		}
		public void setSaleNo(String saleNo) {
			this.saleNo = saleNo;
		}
		public String getPlatformType() {
			return platformType;
		}
		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}
		
	}
	
	
}
