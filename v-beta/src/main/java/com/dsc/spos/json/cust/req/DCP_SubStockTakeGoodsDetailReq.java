package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/*
 * 服务函数：DCP_SubStockTakeGoodsDetail
 * 服务说明：商品录入历程
 * @author jinzma
 * @since  2021-03-05
 */
public class DCP_SubStockTakeGoodsDetailReq extends JsonBasicReq {
    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String subStockTakeNo;
        private String pluNo;
        private String featureNo;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }

        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }

        public String getPluNo() {
            return pluNo;
        }

        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }

        public String getFeatureNo() {
            return featureNo;
        }

        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }
    }
}
