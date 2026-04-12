package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * 服务函数：DCP_ShopGoodsUnitQuery
 * 服务说明：【门店用】商品单位查询
 * @author jinzma
 * @since 2020-08-17
 */
public class DCP_ShopGoodsUnitQueryRes extends JsonRes{
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String unit;
		private String unitName;
		private String udLength;
		private String baseUnit;
		private String baseUnitName;
		private String baseUnitUdLength;
		private String unitRatio;
		private String price;
		private String distriPrice;

        private String purPrice;
        private String minQty;
        private String mulQty;
        private String minRate;
        private String maxRate;
        private String custPrice;
        private String custCategoryDiscRate;

        private List<PurPriceList> purPriceList;

		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getUdLength() {
			return udLength;
		}
		public void setUdLength(String udLength) {
			this.udLength = udLength;
		}
		public String getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit) {
			this.baseUnit = baseUnit;
		}
		public String getBaseUnitName() {
			return baseUnitName;
		}
		public void setBaseUnitName(String baseUnitName) {
			this.baseUnitName = baseUnitName;
		}
		public String getUnitRatio() {
			return unitRatio;
		}
		public void setUnitRatio(String unitRatio) {
			this.unitRatio = unitRatio;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getBaseUnitUdLength() {
			return baseUnitUdLength;
		}
		public void setBaseUnitUdLength(String baseUnitUdLength) {
			this.baseUnitUdLength = baseUnitUdLength;
		}

        public String getPurPrice() {
            return purPrice;
        }

        public void setPurPrice(String purPrice) {
            this.purPrice = purPrice;
        }

        public String getMinQty() {
            return minQty;
        }

        public void setMinQty(String minQty) {
            this.minQty = minQty;
        }

        public String getMulQty() {
            return mulQty;
        }

        public void setMulQty(String mulQty) {
            this.mulQty = mulQty;
        }

        public String getMinRate() {
            return minRate;
        }

        public void setMinRate(String minRate) {
            this.minRate = minRate;
        }

        public String getMaxRate() {
            return maxRate;
        }

        public void setMaxRate(String maxRate) {
            this.maxRate = maxRate;
        }

        public String getCustPrice() {
            return custPrice;
        }

        public void setCustPrice(String custPrice) {
            this.custPrice = custPrice;
        }

        public String getCustCategoryDiscRate() {
            return custCategoryDiscRate;
        }

        public void setCustCategoryDiscRate(String custCategoryDiscRate) {
            this.custCategoryDiscRate = custCategoryDiscRate;
        }

        public List<PurPriceList> getPurPriceList() {
            return purPriceList;
        }

        public void setPurPriceList(List<PurPriceList> purPriceList) {
            this.purPriceList = purPriceList;
        }
    }

    @Data
    public class PurPriceList{
        private String beginQty;
        private String endQty;
        private String purPrice;
    }

}
