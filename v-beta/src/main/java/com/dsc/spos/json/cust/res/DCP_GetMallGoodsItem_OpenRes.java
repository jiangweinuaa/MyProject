package com.dsc.spos.json.cust.res;

import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_GetMallGoodsItem_Open
 * 服务说明：获取线上商品子商品
 * @author jinzma 
 * @since  2020-10-12
 */
public class DCP_GetMallGoodsItem_OpenRes extends JsonRes{
	private level1Elm datas;
	public level1Elm getDatas() {
		return datas;
	}
	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private List<level2ElmAttrs> attrs;
		private List<level2ElmGoods> goods;

		public List<level2ElmAttrs> getAttrs() {
			return attrs;
		}
		public void setAttrs(List<level2ElmAttrs> attrs) {
			this.attrs = attrs;
		}
		public List<level2ElmGoods> getGoods() {
			return goods;
		}
		public void setGoods(List<level2ElmGoods> goods) {
			this.goods = goods;
		}
	}

	public class level2ElmAttrs{
		private String serialNo;
		private String attrId;
		private String attrName;
		private List<level3ElmValues> values;

		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		public String getAttrId() {
			return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		public List<level3ElmValues> getValues() {
			return values;
		}
		public void setValues(List<level3ElmValues> values) {
			this.values = values;
		}
	}	
	public class level3ElmValues{
		private String serialNo;
		private String attrValueId;
		private String attrValueName;
		private String picUrl;

		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		public String getAttrValueId() {
			return attrValueId;
		}
		public void setAttrValueId(String attrValueId) {
			this.attrValueId = attrValueId;
		}
		public String getAttrValueName() {
			return attrValueName;
		}
		public void setAttrValueName(String attrValueName) {
			this.attrValueName = attrValueName;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}

	}

	public class level2ElmGoods{
		private String mallGoodsId;
		private String mallGoodsName;
		private String subGoodsId;
		private String goodsId;
		private String unit;
		private String attrId1;
		private String attrValue1;
		private String attrValue1Name;
		private String attrId2;
		private String attrValue2;
		private String attrValue2Name;
		private String attrId3;
		private String attrValue3;
		private String attrValue3Name;
		private String oriPrice;
		private String price;
		private String stock;
		private String picUrl;
		private List<Map<String, Object>> activity;

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
		public String getSubGoodsId() {
			return subGoodsId;
		}
		public void setSubGoodsId(String subGoodsId) {
			this.subGoodsId = subGoodsId;
		}
		public String getGoodsId() {
			return goodsId;
		}
		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getAttrId1() {
			return attrId1;
		}
		public void setAttrId1(String attrId1) {
			this.attrId1 = attrId1;
		}
		public String getAttrValue1() {
			return attrValue1;
		}
		public void setAttrValue1(String attrValue1) {
			this.attrValue1 = attrValue1;
		}
		public String getAttrValue1Name() {
			return attrValue1Name;
		}
		public void setAttrValue1Name(String attrValue1Name) {
			this.attrValue1Name = attrValue1Name;
		}
		public String getAttrId2() {
			return attrId2;
		}
		public void setAttrId2(String attrId2) {
			this.attrId2 = attrId2;
		}
		public String getAttrValue2() {
			return attrValue2;
		}
		public void setAttrValue2(String attrValue2) {
			this.attrValue2 = attrValue2;
		}
		public String getAttrValue2Name() {
			return attrValue2Name;
		}
		public void setAttrValue2Name(String attrValue2Name) {
			this.attrValue2Name = attrValue2Name;
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
		public String getStock() {
			return stock;
		}
		public void setStock(String stock) {
			this.stock = stock;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public List<Map<String, Object>> getActivity() {
			return activity;
		}
		public void setActivity(List<Map<String, Object>> activity) {
			this.activity = activity;
		}
		public String getAttrId3() {
			return attrId3;
		}
		public void setAttrId3(String attrId3) {
			this.attrId3 = attrId3;
		}
		public String getAttrValue3() {
			return attrValue3;
		}
		public void setAttrValue3(String attrValue3) {
			this.attrValue3 = attrValue3;
		}
		public String getAttrValue3Name() {
			return attrValue3Name;
		}
		public void setAttrValue3Name(String attrValue3Name) {
			this.attrValue3Name = attrValue3Name;
		}
	}

}
