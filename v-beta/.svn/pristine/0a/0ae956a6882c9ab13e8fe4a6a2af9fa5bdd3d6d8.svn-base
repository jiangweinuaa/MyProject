package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CreditQRcodeQueryReq extends JsonBasicReq
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
        private String pay_type;   //#P1 微信 #P2 支付宝
        private String pay_amt;
       // private String allow_pay_type;       // 逗号隔开，可空，空表示都允许 当pay_type为空时，POS端有促销时跟支 付方式互斥时填写。
        private String trade_type;           // NATIVE -Native支付 JSAPI -JSAPI支付  空默认是NATIVE
        private String appid;                // 微信appid，微信JSAPI时必填
        private String openid;               // 微信openid微信JSAPI时必填
        
        
        public String getTrade_type() {
			return trade_type;
		}
		public void setTrade_type(String trade_type) {
			this.trade_type = trade_type;
		}
		public String getAppid() {
			return appid;
		}
		public void setAppid(String appid) {
			this.appid = appid;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getPay_type() {
            return pay_type;
        }
        public void setPay_type(String pay_type) {
            this.pay_type = pay_type;
        }
        public String getPay_amt() {
            return pay_amt;
        }
        public void setPay_amt(String pay_amt) {
            this.pay_amt = pay_amt;
        }

    }


	
}
