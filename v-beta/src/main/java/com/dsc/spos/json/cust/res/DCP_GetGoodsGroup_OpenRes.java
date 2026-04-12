package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * 服务函数：DCP_GetGoodsGroup_Open
 * 服务说明：获取线上商品分组信息(含商品）
 * @author jinzma
 * @since  2020-09-10
 */
public class DCP_GetGoodsGroup_OpenRes extends JsonRes{
	//【ID1031885】【货郎3.2.0.3】商城搜索商品【酒精】，点击购物车报错 by jinzma 20230316
	private String pluOut;
	
	public String getPluOut() {
		return pluOut;
	}
	public void setPluOut(String pluOut) {
		this.pluOut = pluOut;
	}
	private List<level1Elm> datas;
 
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String groupId;
		private String groupName;
		// ADD isShare 增加反参isShare，判断分组是否可分享 BY 2021/6/30
		private String isShare;
		//【ID1031885】【货郎3.2.0.3】商城搜索商品【酒精】，点击购物车报错 by jinzma 20230317
		private String levelId;
		private List<level2Elm> groupGoods;
		
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public List<level2Elm> getGroupGoods() {
			return groupGoods;
		}
		public void setGroupGoods(List<level2Elm> groupGoods) {
			this.groupGoods = groupGoods;
		}
		public String getIsShare() {
			return isShare;
		}
		public void setIsShare(String isShare) {
			this.isShare = isShare;
		}
		public String getLevelId() {
			return levelId;
		}
		public void setLevelId(String levelId) {
			this.levelId = levelId;
		}
	}
	public class level2Elm{
		private String groupId;
		private String groupName;
		private String mallGoodsId;
		private String mallGoodsName;
		private String pluType;
		private String unit;
		private String description;
		private String picUrl;
		private String oriPrice;
		private String price;
		private String basicPrice;
		private String promLable;
		private String symbolDisplay;
		private String symbolType;
		private String symbolIcon;
		private String symbolText;
		private String totalStock;
		private String totalSales;
		@JsonIgnore
		private String createTime; // 上架时间排序 此字段不返回展示
		private String deliveryDescription;
		private String deliveryDescription2;
		
		public String getDeliveryDescription2() {
			return deliveryDescription2;
		}
		public void setDeliveryDescription2(String deliveryDescription2) {
			this.deliveryDescription2 = deliveryDescription2;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getTotalSales() {
			return totalSales;
		}
		public void setTotalSales(String totalSales) {
			this.totalSales = totalSales;
		}
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public String getMallGoodsId() {
			return mallGoodsId;
		}
		public void setMallGoodsId(String mallGoodsId) {
			this.mallGoodsId = mallGoodsId;
		}
		public String getMallGoodsName() {
			return mallGoodsName;
		}
		public void setMallGoodsName(String mallGoodsName) {
			this.mallGoodsName = mallGoodsName;
		}
		public String getPluType() {
			return pluType;
		}
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public String getOriPrice() {
			return oriPrice;
		}
		public void setOriPrice(String oriPrice) {
			this.oriPrice = oriPrice;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getBasicPrice() {
			return basicPrice;
		}
		public void setBasicPrice(String basicPrice) {
			this.basicPrice = basicPrice;
		}
		public String getPromLable() {
			return promLable;
		}
		public void setPromLable(String promLable) {
			this.promLable = promLable;
		}
		public String getSymbolDisplay() {
			return symbolDisplay;
		}
		public void setSymbolDisplay(String symbolDisplay) {
			this.symbolDisplay = symbolDisplay;
		}
		public String getSymbolType() {
			return symbolType;
		}
		public void setSymbolType(String symbolType) {
			this.symbolType = symbolType;
		}
		public String getSymbolIcon() {
			return symbolIcon;
		}
		public void setSymbolIcon(String symbolIcon) {
			this.symbolIcon = symbolIcon;
		}
		public String getSymbolText() {
			return symbolText;
		}
		public void setSymbolText(String symbolText) {
			this.symbolText = symbolText;
		}
		public String getTotalStock() {
			return totalStock;
		}
		public void setTotalStock(String totalStock) {
			this.totalStock = totalStock;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getDeliveryDescription() {
			return deliveryDescription;
		}
		public void setDeliveryDescription(String deliveryDescription) {
			this.deliveryDescription = deliveryDescription;
		}
	}
}
