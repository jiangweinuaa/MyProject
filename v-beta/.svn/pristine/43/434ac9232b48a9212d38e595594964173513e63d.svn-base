package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_GoodsOnlineQueryRes  extends JsonRes {
	
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm {
		private String pluNo;
		private String pluName;
		private String pluType;      //商品类型：NORMAL-普通商品 FEATURE-特征码商品 PACKAGE -套餐商品MULTISPEC-多规格商品
		private String listImage;                            //商品图片名称
		private String listImageUrl;                         //商品图片地址
		private String minPrice;                             //最小价格
		private String maxPrice;                             //最大价格
		private String status;                               //状态：100-已上架0-已下架
		private String sortId;                               //显示顺序，从大到小排序
		private List<classMemu> classList;                   //隶属菜单
		private List<channel> channelList;                   //隶属菜单
		
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
		public String getPluType() {
			return pluType;
		}
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getListImageUrl() {
			return listImageUrl;
		}
		public void setListImageUrl(String listImageUrl) {
			this.listImageUrl = listImageUrl;
		}
		public String getMinPrice() {
			return minPrice;
		}
		public void setMinPrice(String minPrice) {
			this.minPrice = minPrice;
		}
		public String getMaxPrice() {
			return maxPrice;
		}
		public void setMaxPrice(String maxPrice) {
			this.maxPrice = maxPrice;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<classMemu> getClassList() {
			return classList;
		}
		public void setClassList(List<classMemu> classList) {
			this.classList = classList;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public List<channel> getChannelList() {
			return channelList;
		}
		public void setChannelList(List<channel> channelList) {
			this.channelList = channelList;
		}
	}
	
	public class classMemu {
		private String classNo;       //菜单编码
		private String className;     //菜单名称
		
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		
	}
	public class channel {
		private String channelId;
		private String channelName;
		private String status;
		
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}
	
}
