package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：PTemplateGet
 *    說明：要货模板查询
 * 服务说明：要货模板查询
 * @author jinzma
 * @since  2017-03-09
 */
public class DCP_PTemplateQueryReq extends JsonBasicReq {

    private levelElm request;
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String keyTxt;
        private String docType;
        private String supplierId;

        private String bDate;
        private String queryType;

        private String isCheckUser;

        public String getKeyTxt() {
            return keyTxt;
        }
        public void setKeyTxt(String keyTxt) {
            this.keyTxt = keyTxt;
        }
        public String getDocType() {
            return docType;
        }
        public void setDocType(String docType) {
            this.docType = docType;
        }
        public String getSupplierId() {
            return supplierId;
        }
        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getbDate() {
            return bDate;
        }

        public void setbDate(String bDate) {
            this.bDate = bDate;
        }

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getIsCheckUser() {
            return isCheckUser;
        }

        public void setIsCheckUser(String isCheckUser) {
            this.isCheckUser = isCheckUser;
        }
    }
}
