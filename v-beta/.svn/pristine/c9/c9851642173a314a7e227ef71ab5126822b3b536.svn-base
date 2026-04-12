package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

import java.util.List;

public class DCP_OrderOperateAgreeOrRejectRes extends JsonBasicRes {

    public levRes datas;

    public levRes getDatas() {
        return datas;
    }

    public void setDatas(levRes datas) {
        this.datas = datas;
    }

    public class levRes{
        private List<ErrorOrderList> errorOrderList;

        public List<ErrorOrderList> getErrorOrderList() {
            return errorOrderList;
        }

        public void setErrorOrderList(List<ErrorOrderList> errorOrderList) {
            this.errorOrderList = errorOrderList;
        }
    }


    public class ErrorOrderList{
        private String orderNo;
        private String errorDesc;
        public String getOrderNo() {
            return orderNo;
        }
        public String getErrorDesc() {
            return errorDesc;
        }
        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
        public void setErrorDesc(String errorDesc) {
            this.errorDesc = errorDesc;
        }

    }
}
