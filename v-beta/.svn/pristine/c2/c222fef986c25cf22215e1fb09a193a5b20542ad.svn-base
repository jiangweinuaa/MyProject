package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.cust.JsonReq;

/**
 * 卖家取消订单
 * @author yuanyy 2019-07-01
 *
 */
public class DCP_OrderECSellerCancelReq extends JsonReq {
	/**
	 * {		
		   "serviceId": "OrderECSellerCancelDCP",	必传，服务名	
		   "token": "f14ee75ff5b220177ac0dc538bdea08c",	必传且非空，访问令牌	
		   "datas": [		
		        {		
		             "ecPlatformNo": ""	电商平台代码	
		             "ecOrderNo": "",	订单号	
		             "cancelReason": "",	取消原因代码 001:卖家缺货 002：客户要求 003：无法送达的区域 004：不支持的付款方式	
		         }		
		 ]		
		}		

	 */
	
	private List<level1Elm> datas;
	
	public  class level1Elm{
		private String ecPlatformNo;
		private String ecOrderNo;
		private String cancelReason;
		public String getEcPlatformNo() {
			return ecPlatformNo;
		}
		public void setEcPlatformNo(String ecPlatformNo) {
			this.ecPlatformNo = ecPlatformNo;
		}
		public String getEcOrderNo() {
			return ecOrderNo;
		}
		public void setEcOrderNo(String ecOrderNo) {
			this.ecOrderNo = ecOrderNo;
		}
		public String getCancelReason() {
			return cancelReason;
		}
		public void setCancelReason(String cancelReason) {
			this.cancelReason = cancelReason;
		}
		
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
}
