package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * @author wangdingjun
 */
public class DCP_PackChargeCreateReq extends JsonBasicReq {

    private DCP_PackChargeCreateReq.levelElm request;

    public DCP_PackChargeCreateReq.levelElm getRequest() {
        return request;
    }

    public void setRequest(DCP_PackChargeCreateReq.levelElm request) {
        this.request = request;
    }

    public class levelElm {

        private String packPluNo;

        private String memo;
        private String packBagNum;
        private String packPluType;
        private List<Goods> goodsList;

        public String getPackBagNum() {
			return packBagNum;
		}

		public void setPackBagNum(String packBagNum) {
			this.packBagNum = packBagNum;
		}

		public String getPackPluType() {
			return packPluType;
		}

		public void setPackPluType(String packPluType) {
			this.packPluType = packPluType;
		}

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

        public List<Goods> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<Goods> goodsList) {
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
