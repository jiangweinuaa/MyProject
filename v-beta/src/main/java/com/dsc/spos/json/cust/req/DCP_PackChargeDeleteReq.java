package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * @author wangdingjun
 */
public class DCP_PackChargeDeleteReq extends JsonBasicReq {

    private DCP_PackChargeDeleteReq.levelElm request;

    public DCP_PackChargeDeleteReq.levelElm getRequest() {
        return request;
    }

    public void setRequest(DCP_PackChargeDeleteReq.levelElm request) {
        this.request = request;
    }

    public class levelElm {

        private String packPluNo;

        public String getPackPluNo() {
            return packPluNo;
        }

        public void setPackPluNo(String packPluNo) {
            this.packPluNo = packPluNo;
        }
    }

}
