package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CreditPayResultQueryReq extends JsonBasicReq
{

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String pay_type;
        private String order_id;
        private String trade_no;
        private String shop_code;
        private String appId;
        private String eid;
        
        public String getEid() {
			return eid;
		}
		public void setEid(String eid) {
			this.eid = eid;
		}
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public String getTrade_no() {
			return trade_no;
		}
		public void setTrade_no(String trade_no) {
			this.trade_no = trade_no;
		}
		public String getShop_code() {
			return shop_code;
		}
		public void setShop_code(String shop_code) {
			this.shop_code = shop_code;
		}
		public String getPay_type() {
            return pay_type;
        }
        public void setPay_type(String pay_type) {
            this.pay_type = pay_type;
        }
        public String getOrder_id() {
            return order_id;
        }
        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }
    }
	
}
