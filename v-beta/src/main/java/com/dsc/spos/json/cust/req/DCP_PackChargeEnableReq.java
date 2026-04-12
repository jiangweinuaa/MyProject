package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PackChargeEnableReq extends JsonBasicReq {

    private DCP_PackChargeEnableReq.levelElm request;

    public DCP_PackChargeEnableReq.levelElm getRequest() {
        return request;
    }

    public void setRequest(DCP_PackChargeEnableReq.levelElm request) {
        this.request = request;
    }

    public class levelElm {

        private String packPluNo;

        private String oprType;

        public String getPackPluNo() {
            return packPluNo;
        }

        public void setPackPluNo(String packPluNo) {
            this.packPluNo = packPluNo;
        }

        public String getOprType() {
            return oprType;
        }

        public void setOprType(String oprType) {
            this.oprType = oprType;
        }
    }

}
