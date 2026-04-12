package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务名称：DCP_SalePriceAdjustQuery
 * 服务说明：自建门店调价查询(零售价)
 * @author jinzma
 * @since  2022-02-23
 */
public class DCP_SalePriceAdjustQueryReq  extends JsonBasicReq {
   private levelElm request;
   
   public levelElm getRequest() {
      return request;
   }
   public void setRequest(levelElm request) {
      this.request = request;
   }
   
   public class levelElm{
      private String status;
      private String keyTxt;
      private String type;
      private String beginDate;
      private String endDate;
      
      public String getStatus() {
         return status;
      }
      public void setStatus(String status) {
         this.status = status;
      }
      public String getKeyTxt() {
         return keyTxt;
      }
      public void setKeyTxt(String keyTxt) {
         this.keyTxt = keyTxt;
      }
      public String getType() {
         return type;
      }
      public void setType(String type) {
         this.type = type;
      }
      public String getBeginDate() {
         return beginDate;
      }
      public void setBeginDate(String beginDate) {
         this.beginDate = beginDate;
      }
      public String getEndDate() {
         return endDate;
      }
      public void setEndDate(String endDate) {
         this.endDate = endDate;
      }
   }
}
