package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_RefundSetQueryRes extends JsonRes {

	private String appId;// 公众号id
	private Level1Elm datas;
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Level1Elm getDatas() {
		return datas;
	}

	public void setDatas(Level1Elm datas) {
		this.datas = datas;
	}

	public class Level1Elm{
		private String eId;//
		private String appId;//
		private Integer applyRefund;//申请退单	
		private Integer applyRefundDelivery;//发货后申请退单
		private Integer applyRefundLimit;//申请退单期限
		private Integer cancelLimit;//自动取消退货期限
        private String refundMember;//申请退单次数	
        private String autoRefund;//自动审核退单
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public Integer getApplyRefund() {
			return applyRefund;
		}
		public void setApplyRefund(Integer applyRefund) {
			this.applyRefund = applyRefund;
		}
		public Integer getApplyRefundDelivery() {
			return applyRefundDelivery;
		}
		public void setApplyRefundDelivery(Integer applyRefundDelivery) {
			this.applyRefundDelivery = applyRefundDelivery;
		}
		public Integer getApplyRefundLimit() {
			return applyRefundLimit;
		}
		public void setApplyRefundLimit(Integer applyRefundLimit) {
			this.applyRefundLimit = applyRefundLimit;
		}
		public Integer getCancelLimit() {
			return cancelLimit;
		}
		public void setCancelLimit(Integer cancelLimit) {
			this.cancelLimit = cancelLimit;
		}
		public String getRefundMember() {
			return refundMember;
		}
		public void setRefundMember(String refundMember) {
			this.refundMember = refundMember;
		}
		public String getAutoRefund() {
			return autoRefund;
		}
		public void setAutoRefund(String autoRefund) {
			this.autoRefund = autoRefund;
		}
	}

}
