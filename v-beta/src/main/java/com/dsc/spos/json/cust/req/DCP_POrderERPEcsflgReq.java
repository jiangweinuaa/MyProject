package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_POrderERPEcsflg
 * 服务说明：要货单结案（ERP下发）
 * @author jinzma
 * @since  2023-07-03
 */
public class DCP_POrderERPEcsflgReq extends JsonBasicReq {
    private String loadDocNo;
    
    public String getLoadDocNo() {
        return loadDocNo;
    }
    public void setLoadDocNo(String loadDocNo) {
        this.loadDocNo = loadDocNo;
    }
}
