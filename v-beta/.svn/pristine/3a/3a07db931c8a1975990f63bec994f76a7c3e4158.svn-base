package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_BillDateAlterReq.NameLang;
import com.dsc.spos.json.cust.req.DCP_BillDateAlterReq.levelRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * 供应商证照异动单 
 * 
 * @date 2024-10-19
 * @author 01029
 */
@Getter
@Setter
public class DCP_SupLicenseApplyCreateReq extends JsonBasicReq {

	private levelRequest request;

	@Getter
	@Setter
	public class levelRequest {
		private String oprType 		;
		private String orgNo        ;
		private String bDate        ;
		private String billNo_ID    ;
		private String employeeID   ;
		private String departID     ;
		private String memo         ;


		private List<Detail1> detail;
 

	}

	@Getter
	@Setter
	public class Detail1 {
		private String  item		;
		private String  supplierNo  ;
		private String  licenseType ;
		private String  licenseNo   ;
		private String  beginDate   ;
		private String  endDate     ;
		private String  licenseImg  ;
		private String  status      ;

	}
 

}
