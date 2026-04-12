package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 服务函数：DCP_GetMallGoodsList_Open
 * 服务说明：获取线上商品列表
 * @author jinzma
 * @since  2020-09-27
 */
public class DCP_GetMallGoodsList_OpenRes extends JsonRes{
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
    public class level1Elm{
		private String mallGoodsId;
		private String mallGoodsName;
		private String pluType;
		private String unit;
		private String description;
		private String picUrl;
		private String groupId;
		private String groupName;
		private String oriPrice;
		private String price;
		private String symbolDisplay;
		private String symbolType;
		private String symbolIcon;
		private String symbolText;
		private String promLable;
		private String totalStock;
		private String totalSales;
		private String isCollect;
		private String collectQty;
		@JsonIgnore
		private String createTime; // 上架时间排序 此字段不返回展示
        private String deliveryDescription;
        private String deliveryDescription2;
		private String deliveryStock;
		private String deliveryShopId;
		private String shopStock;
		
		public String getShopStock() {
			return shopStock;
		}
		public void setShopStock(String shopStock) {
			this.shopStock = shopStock;
		}
		public String getDeliveryDescription2() {
			return deliveryDescription2;
		}
		public void setDeliveryDescription2(String deliveryDescription2) {
			this.deliveryDescription2 = deliveryDescription2;
		}
		public String getDeliveryShopId() {
			return deliveryShopId;
		}
		public void setDeliveryShopId(String deliveryShopId) {
			this.deliveryShopId = deliveryShopId;
		}
		public String getDeliveryStock() {
			return deliveryStock;
		}
		public void setDeliveryStock(String deliveryStock) {
			this.deliveryStock = deliveryStock;
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
		public String getPromLable() {
			return promLable;
		}
		public void setPromLable(String promLable) {
			this.promLable = promLable;
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
		public String getTotalSales() {
			return totalSales;
		}
		public void setTotalSales(String totalSales) {
			this.totalSales = totalSales;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getIsCollect() {
			return isCollect;
		}
		public void setIsCollect(String isCollect) {
			this.isCollect = isCollect;
		}
		public String getCollectQty() {
			return collectQty;
		}
		public void setCollectQty(String collectQty) {
			this.collectQty = collectQty;
		}
		public String getDeliveryDescription() {
			return deliveryDescription;
		}
		public void setDeliveryDescription(String deliveryDescription) {
			this.deliveryDescription = deliveryDescription;
		}
	}
}
