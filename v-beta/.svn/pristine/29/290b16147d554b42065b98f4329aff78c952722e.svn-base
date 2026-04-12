package com.dsc.spos.thirdpart.youzan.response;

/**
 * 通过提货码获取订单信息：https://doc.youzanyun.com/doc#/content/API/1-429/detail/api/0/179
 * 通过提货码核销订单：https://doc.youzanyun.com/doc#/content/API/1-429/detail/api/0/139
 * 通过订单号核销订单：https://doc.youzanyun.com/doc#/content/API/1-429/detail/api/0/1072
 * 用于核销到店自提订单
 * /api/youzan.trade.selffetchcode.apply/3.0.0
 * @author LN 08546
 */
public class YouZanSelfFetchCodeApplyRes {

	public YouZanSelfFetchCodeApplyRes() {
		
	}
	
	private Response response;
	private ErrorResponse error_response;

	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	public ErrorResponse getError_response() {
		return error_response;
	}
	public void setError_response(ErrorResponse error_response) {
		this.error_response = error_response;
	}
	public class Response{
		private String is_success;

		public String getIs_success() {
			return is_success;
		}

		public void setIs_success(String is_success) {
			this.is_success = is_success;
		}
	}
	public class ErrorResponse{
		private String msg;
		private String code;
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		
	}
	

}
