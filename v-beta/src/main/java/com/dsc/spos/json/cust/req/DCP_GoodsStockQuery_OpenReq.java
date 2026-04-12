package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_GoodsStockQuery
 * 服务说明：商品库存查询
 * @author jinzma
 * @since  2020-04-21
 */
public class DCP_GoodsStockQuery_OpenReq extends JsonBasicReq {
    private levelElm request;
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String eId;
        private String queryOrgId;
        private String queryDate;
        private String queryType;
        private String warehouse;
        private String stockQtyType;
        private List<level1Elm> pluList;

        public String geteId() {
            return eId;
        }
        public void seteId(String eId) {
            this.eId = eId;
        }
        public String getQueryType() {
            return queryType;
        }
        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }
        public String getWarehouse() {
            return warehouse;
        }
        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }
        public String getStockQtyType() {
            return stockQtyType;
        }
        public void setStockQtyType(String stockQtyType) {
            this.stockQtyType = stockQtyType;
        }
        public String getQueryOrgId() {
            return queryOrgId;
        }
        public void setQueryOrgId(String queryOrgId) {
            this.queryOrgId = queryOrgId;
        }
        public String getQueryDate() {
            return queryDate;
        }
        public void setQueryDate(String queryDate) {
            this.queryDate = queryDate;
        }
        public List<level1Elm> getPluList() {
            return pluList;
        }
        public void setPluList(List<level1Elm> pluList) {
            this.pluList = pluList;
        }
    }
    public class level1Elm{
        private String pluNo;
        private String featureNo;
        private String batchNo;

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
        public String getBatchNo() {
            return batchNo;
        }
        public void setBatchNo(String batchNo) {
            this.batchNo = batchNo;
        }
    }

}
