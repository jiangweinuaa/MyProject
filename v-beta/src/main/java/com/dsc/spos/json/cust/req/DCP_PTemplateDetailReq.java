package com.dsc.spos.json.cust.req;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：PTemplateGet
 *    說明：要货模板查询
 * 服务说明：要货模板查询
 * @author jinzma
 * @since  2017-03-09
 */
public class DCP_PTemplateDetailReq extends JsonBasicReq {

    private levelElm request;
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String docType;
        private String pTemplateNo;
        private String isrefBaseQty; //Y.返回库存数  N.不返回库存数
        private String warehouse;
        private String rangeWay;
        private String isShowZStock;
        private String IsStockMultipleUnit;
        /**
         * 是否查询库存量Y/N
         */
        private String queryStockqty;
        private String queryStockWarehouse;


        public String getQueryStockqty()
        {
            return queryStockqty;
        }
        public void setQueryStockqty(String queryStockqty)
        {
            this.queryStockqty = queryStockqty;
        }
        public String getDocType() {
            return docType;
        }
        public void setDocType(String docType) {
            this.docType = docType;
        }
        public String getpTemplateNo() {
            return pTemplateNo;
        }
        public void setpTemplateNo(String pTemplateNo) {
            this.pTemplateNo = pTemplateNo;
        }
        public String getIsrefBaseQty() {
            return isrefBaseQty;
        }
        public void setIsrefBaseQty(String isrefBaseQty) {
            this.isrefBaseQty = isrefBaseQty;
        }
        public String getWarehouse() {
            return warehouse;
        }
        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }
        public String getRangeWay() {
            return rangeWay;
        }
        public void setRangeWay(String rangeWay) {
            this.rangeWay = rangeWay;
        }
        public String getIsShowZStock() {
            return isShowZStock;
        }
        public void setIsShowZStock(String isShowZStock) {
            this.isShowZStock = isShowZStock;
        }
        public String getIsStockMultipleUnit() {
            return IsStockMultipleUnit;
        }
        public void setIsStockMultipleUnit(String isStockMultipleUnit) {
            IsStockMultipleUnit = isStockMultipleUnit;
        }
        public String getQueryStockWarehouse() {
            return queryStockWarehouse;
        }
        public void setQueryStockWarehouse(String queryStockWarehouse) {
            this.queryStockWarehouse = queryStockWarehouse;
        }
    }
}
