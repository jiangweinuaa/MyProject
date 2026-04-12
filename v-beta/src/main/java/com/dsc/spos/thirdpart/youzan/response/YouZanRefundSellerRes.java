package com.dsc.spos.thirdpart.youzan.response;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家主动退款API
 * https://doc.youzanyun.com/doc#/content/API/1-403/detail/api/0/148
 * @author LN 08546
 */
public class YouZanRefundSellerRes extends YouZanBasicRes  {
		
	public YouZanRefundSellerRes() {
	}
	private List<OidInfo> responseDTO=new ArrayList<OidInfo>();

	public List<OidInfo> getResponseDTO() {
		return responseDTO;
	}

	public void setResponseDTO(List<OidInfo> responseDTO) {
		this.responseDTO = responseDTO;
	}

	public class OidInfo{
		private String oid;
		private String refund_id;
		private String is_success;
		public String getOid() {
			return oid;
		}
		public void setOid(String oid) {
			this.oid = oid;
		}
		public String getRefund_id() {
			return refund_id;
		}
		public void setRefund_id(String refund_id) {
			this.refund_id = refund_id;
		}
		public String getIs_success() {
			return is_success;
		}
		public void setIs_success(String is_success) {
			this.is_success = is_success;
		}
	}
}
