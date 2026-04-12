package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PackChargeQueryRes extends JsonRes {
	
	private List<level1Elm> datas ;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public static class level1Elm{
		
		private String packPluNo;
		
		private String packPluName;
		
		private String packUnitId;
		
		private String packUnitName;
		
		private Double packPrice;
		
		private String memo;
		
		private String status;
		
		private String redisUpdateSuccess;
		private String packPluType;
		private String packBagNum;
		private List<Goods> goodsList;

		public String getPackPluType() {
			return packPluType;
		}

		public void setPackPluType(String packPluType) {
			this.packPluType = packPluType;
		}

		public String getPackBagNum() {
			return packBagNum;
		}

		public void setPackBagNum(String packBagNum) {
			this.packBagNum = packBagNum;
		}

		public String getPackPluNo() {
			return packPluNo;
		}

		public void setPackPluNo(String packPluNo) {
			this.packPluNo = packPluNo;
		}

		public String getPackPluName() {
			return packPluName;
		}

		public void setPackPluName(String packPluName) {
			this.packPluName = packPluName;
		}

		public String getPackUnitId() {
			return packUnitId;
		}

		public void setPackUnitId(String packUnitId) {
			this.packUnitId = packUnitId;
		}

		public String getPackUnitName() {
			return packUnitName;
		}

		public void setPackUnitName(String packUnitName) {
			this.packUnitName = packUnitName;
		}

		public Double getPackPrice() {
			return packPrice;
		}

		public void setPackPrice(Double packPrice) {
			this.packPrice = packPrice;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getRedisUpdateSuccess() {
			return redisUpdateSuccess;
		}

		public void setRedisUpdateSuccess(String redisUpdateSuccess) {
			this.redisUpdateSuccess = redisUpdateSuccess;
		}

		public List<Goods> getGoodsList() {
			return goodsList;
		}

		public void setGoodsList(List<Goods> goodsList) {
			this.goodsList = goodsList;
		}
		
	}
	
	public static class Goods{
		
		private String pluNo;
		
		private String pluName;
		
		private String unitId;
		
		private String unitName;

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

		public String getUnitId() {
			return unitId;
		}

		public void setUnitId(String unitId) {
			this.unitId = unitId;
		}

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		
	}
	
}
