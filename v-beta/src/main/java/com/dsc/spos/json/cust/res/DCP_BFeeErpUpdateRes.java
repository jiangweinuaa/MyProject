package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 服务函数：DCP_BFeeErpUpdate
 * 服务说明：费用单状态更新
 * @author jinzma
 * @since  2022-07-15
 */
public class DCP_BFeeErpUpdateRes extends JsonBasicRes {
    private String doc_no;
    private String org_no;
    
    public String getDoc_no() {
        return doc_no;
    }
    public void setDoc_no(String doc_no) {
        this.doc_no = doc_no;
    }
    public String getOrg_no() {
        return org_no;
    }
    public void setOrg_no(String org_no) {
        this.org_no = org_no;
    }
}
