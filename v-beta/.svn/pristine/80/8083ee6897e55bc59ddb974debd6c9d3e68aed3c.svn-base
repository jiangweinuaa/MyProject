package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_PackChargeUpdateReq extends JsonBasicReq {

    private DCP_PackChargeUpdateReq.levelElm request;

    public DCP_PackChargeUpdateReq.levelElm getRequest() {
        return request;
    }

    public void setRequest(DCP_PackChargeUpdateReq.levelElm request) {
        this.request = request;
    }

    public class levelElm {

        private String packPluNo;

        private String memo;
        private String packBagNum;
        private String packPluType;
        
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

		private List<DCP_PackChargeUpdateReq.Goods> goodsList;

        public String getPackPluNo() {
            return packPluNo;
        }

        public void setPackPluNo(String packPluNo) {
            this.packPluNo = packPluNo;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public List<DCP_PackChargeUpdateReq.Goods> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<DCP_PackChargeUpdateReq.Goods> goodsList) {
            this.goodsList = goodsList;
        }
    }

    public class Goods{

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
