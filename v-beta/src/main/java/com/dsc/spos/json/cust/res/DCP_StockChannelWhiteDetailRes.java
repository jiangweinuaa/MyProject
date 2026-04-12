package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 渠道商品分货白名单查询
 * @author 2020-06-03
 *
 */
public class DCP_StockChannelWhiteDetailRes extends JsonRes {
	
	public levelRes datas;

	public levelRes getDatas() {
		return datas;
	}

	public void setDatas(levelRes datas) {
		this.datas = datas;
	}

	public class levelRes{
		private List<PluList> pluList;

		public List<PluList> getPluList() {
			return pluList;
		}

		public void setPluList(List<PluList> pluList) {
			this.pluList = pluList;
		}
		
	}
	
	public class PluList{
		private String channelId;
		private String channelName;
//		private String organizationNo;
//		private String organizationName;
		private String pluNo;
		private String pluName;
//		private String featureNo;
//		private String featureName;
//		private String sUnit;
//		private String sUnitName;
//		private String warehouse;
//		private String warehouseName;
		private String listImage;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;
		public String getChannelId() {
			return channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public String getPluNo() {
			return pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public String getListImage() {
			return listImage;
		}
		public String getLastModiOpId() {
			return lastModiOpId;
		}
		public String getLastModiOpName() {
			return lastModiOpName;
		}
		public String getLastModiTime() {
			return lastModiTime;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public void setLastModiOpId(String lastModiOpId) {
			this.lastModiOpId = lastModiOpId;
		}
		public void setLastModiOpName(String lastModiOpName) {
			this.lastModiOpName = lastModiOpName;
		}
		public void setLastModiTime(String lastModiTime) {
			this.lastModiTime = lastModiTime;
		}
		
		
	}

	public static levelRes levelRes() {
		// TODO Auto-generated method stub
		return null;
	}

	public DCP_StockChannelWhiteDetailRes(com.dsc.spos.json.cust.res.DCP_StockChannelWhiteDetailRes.levelRes datas) {
		super();
		this.datas = datas;
	}
	
	public DCP_StockChannelWhiteDetailRes() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
