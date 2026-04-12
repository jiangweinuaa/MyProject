package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_CreditERPCreateReq extends JsonBasicReq
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
        private String pay_amt;
        private String payDate;
        private String shopId;
        private String eId;
        
        public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
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
        public String getPay_amt() {
            return pay_amt;
        }
        public void setPay_amt(String pay_amt) {
            this.pay_amt = pay_amt;
        }

        public String getPayDate()
        {
            return payDate;
        }

        public void setPayDate(String payDate)
        {
            this.payDate = payDate;
        }
    }
	
}
