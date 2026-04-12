package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 库存销售锁定解锁日志查询
 * @author 2020-06-08
 *
 */
public class DCP_StockOrderLockDetailRes extends JsonRes {
	
	public levelRes datas;

	public levelRes getDatas() {
		return datas;
	}

	public void setDatas(levelRes datas) {
		this.datas = datas;
	}

	public class levelRes{
		private List<BillList> billList;

		public List<BillList> getBillList() {
			return billList;
		}

		public void setBillList(List<BillList> billList) {
			this.billList = billList;
		}

		
	}
	
	public class BillList{
		
		private String billNo;
		private String item;
		
		private String channelId;
		private String channelName;
		private String organizationNo;
		private String organizationName;
		private String pluNo;
		private String pluName;
		private String featureNo;
		private String featureName;
		private String sUnit;
		private String sUnitName;
		private String warehouse;
		private String warehouseName;
		private String listImage;
//		private String oQty; //下订数
//		private String tQty; //已提货数
		private String sQty; //锁定数
//		private String saleType;//销售模式    share：共享,online：预留（上架）
		
		
		private String memo;
//		private String lockDate; //锁定日期
		private String lockTime; //锁定时间
		
		private String createOpId;
		private String createOpName;
		private String createTime;
		
		public String getBillNo() {
			return billNo;
		}
		public String getItem() {
			return item;
		}
		public String getChannelId() {
			return channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public String getOrganizationNo() {
			return organizationNo;
		}
		public String getOrganizationName() {
			return organizationName;
		}
		public String getPluNo() {
			return pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public String getFeatureName() {
			return featureName;
		}
		public String getsUnit() {
			return sUnit;
		}
		public String getsUnitName() {
			return sUnitName;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public String getListImage() {
			return listImage;
		}
		public String getsQty() {
			return sQty;
		}
		public String getMemo() {
			return memo;
		}
		public String getLockTime() {
			return lockTime;
		}
		public String getCreateOpId() {
			return createOpId;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}
		public void setsUnit(String sUnit) {
			this.sUnit = sUnit;
		}
		public void setsUnitName(String sUnitName) {
			this.sUnitName = sUnitName;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public void setsQty(String sQty) {
			this.sQty = sQty;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public void setLockTime(String lockTime) {
			this.lockTime = lockTime;
		}
		public void setCreateOpId(String createOpId) {
			this.createOpId = createOpId;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		
	}

	public static levelRes levelRes() {
		// TODO Auto-generated method stub
		return null;
	}

	public DCP_StockOrderLockDetailRes(com.dsc.spos.json.cust.res.DCP_StockOrderLockDetailRes.levelRes datas) {
		super();
		this.datas = datas;
	}
	
	public DCP_StockOrderLockDetailRes() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
