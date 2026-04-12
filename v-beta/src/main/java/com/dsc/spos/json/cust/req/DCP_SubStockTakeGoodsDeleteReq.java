package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/*
 * 服务函数：DCP_SubStockTakeGoodsDelete
 * 服务说明：盘点子任务商品删除
 * @author jinzma
 * @since  2021-03-05
 */
public class DCP_SubStockTakeGoodsDeleteReq extends JsonBasicReq {
    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm {
        private String subStockTakeNo;
        private List<level1Elm> pluList;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }

        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }

        public List<level1Elm> getPluList() {
            return pluList;
        }

        public void setPluList(List<level1Elm> pluList) {
            this.pluList = pluList;
        }
    }

    public class level1Elm {
        private String pluNo;
        private String featureNo;

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
