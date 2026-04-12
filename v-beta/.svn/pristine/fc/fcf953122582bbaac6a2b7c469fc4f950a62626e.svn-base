package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.cust.JsonRes;
 
 

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DCP_BizPartnerBillQueryRes extends JsonRes {

	private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private String bizPartnerNo;
    	private String sName;
    	private String fName;
    	private String bizType;
    	private String corpType;
    	private String registerNo;
    	private String legalPerson;
    	private String taxPayerNo;
    	private String mainCategory;
    	private String mainBrands;
    	private String purEmpNo;
    	private String purEmpName;
    	private String purDeptNo;
    	private String purDeptName;
    	private String lifeValue;
    	private String lifeValue_desc;
    	private String grade;
    	private String role;
    	private String enableContract;
    	private String payType;
    	private String payCenter;
    	private String payCenter_desc;
    	private String billDateNo;
    	private String billDate_desc;
    	private String taxCode;
    	private String taxName;
    	private String taxRate;
    	private String invoiceCode;
    	private String invoiceName;
    	private String payDateNo;
    	private String payDate_desc;
    	private String saleEmpNo;
    	private String saleEmpName;
    	private String saleDeptNo;
    	private String saleDeptName;
    	private String custGrade;
    	private String custPayType;
    	private String custPayCenter;
    	private String custPayCenterName;
    	private String custBillDateNo;
    	private String custBillDate_desc;
    	private String custPayDateNo;
    	private String custPayDate_desc;
    	private String saleInvoiceCode;
    	private String saleInvoiceName;
    	private String mainCurrency;
    	private String currName;
    	private String beginDate;
    	private String endDate;
    	private String memo;
    	private String status;
   
           
    	private String creatorID;          
    	private String creatorName;        
    	private String creatorDeptID;      
    	private String creatorDeptName;    
    	private String create_Datetime;    
    	private String lastModifyID;       
    	private String lastModifyName;     
    	private String lastModify_Datetime;
        
    	private List<OrgList> orgList;
		private List<BillList> billList;
    }

   

	@Getter
	@Setter
	public class OrgList {
		private String orgNo;
		private String orgName;
		private String status;
	}

 
	
	@Getter
	@Setter
	public class BillList {
		@JSONFieldRequired(display="组织编号")
		private String orgNo;
		private String orgName;
		private String contractNo;
		private String item;
		private String bDate;
		private String eDate;
		private String isCheck;
		private String billNo;
		private String billType;

	}

 
}
