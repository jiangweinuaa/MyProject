package com.dsc.spos.waimai.jddj;

public class OrderAdjustRespDTOResult {

   private String code;
   private String msg;
   private String detail;
   private OrderAdjustRespDTO result;
   public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

   public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

   public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getDetail() {
        return detail;
    }

   public void setResult(OrderAdjustRespDTO result) {
        this.result = result;
    }
    public OrderAdjustRespDTO getResult() {
        return result;
    }

}