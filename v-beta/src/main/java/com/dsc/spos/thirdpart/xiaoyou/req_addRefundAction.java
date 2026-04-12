package com.dsc.spos.thirdpart.xiaoyou;

public class req_addRefundAction extends baseEntity {
    private String refund_no;
    private String action_desc;

    public String getRefund_no() {
        return refund_no;
    }

    public void setRefund_no(String refund_no) {
        this.refund_no = refund_no;
    }

    public String getAction_desc() {
        return action_desc;
    }

    public void setAction_desc(String action_desc) {
        this.action_desc = action_desc;
    }
}
