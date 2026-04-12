package com.dsc.spos.json.cust.req;

import java.util.List;
import java.util.Map;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：BFeeCreate
 *    說明：门店费用新增
 * 服务说明：门店费用新增
 * @author luoln 
 * @since  2017-07-17
 */
public class DCP_BFeeCreateReq extends JsonBasicReq{

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
	public class levelElm{
		private String bDate;
		private String memo;
		private String status;
		private String docType;
		private String bFeeID;
		private String squadNo;
		private String taxCode;
		private double taxRate;
		private List<Map<String, String>> datas;
		
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getbFeeID() {
			return bFeeID;
		}
		public void setbFeeID(String bFeeID) {
			this.bFeeID = bFeeID;
		}
		public String getSquadNo() {
			return squadNo;
		}
		public void setSquadNo(String squadNo) {
			this.squadNo = squadNo;
		}
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		public double getTaxRate() {
			return taxRate;
		}
		public void setTaxRate(double taxRate) {
			this.taxRate = taxRate;
		}
		public List<Map<String, String>> getDatas() {
			return datas;
		}
		public void setDatas(List<Map<String, String>> datas) {
			this.datas = datas;
		} 
	}
	
	
}
