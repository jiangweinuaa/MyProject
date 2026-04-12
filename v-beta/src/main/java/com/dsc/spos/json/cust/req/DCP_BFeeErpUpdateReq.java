package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import java.util.List;

/**
 * 服务函数：DCP_BFeeErpUpdate
 * 服务说明：费用单状态更新
 * @author jinzma
 * @since  2022-07-15
 */
public class DCP_BFeeErpUpdateReq extends JsonBasicReq {
    
    private String enterprise_no;
    private String shop_no;
    private String front_no;
    private String operation_type;
    
    public String getEnterprise_no() {
        return enterprise_no;
    }
    public void setEnterprise_no(String enterprise_no) {
        this.enterprise_no = enterprise_no;
    }
    public String getShop_no() {
        return shop_no;
    }
    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }
    public String getFront_no() {
        return front_no;
    }
    public void setFront_no(String front_no) {
        this.front_no = front_no;
    }
    public String getOperation_type() {
        return operation_type;
    }
    public void setOperation_type(String operation_type) {
        this.operation_type = operation_type;
    }
}
