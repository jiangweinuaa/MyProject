package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
 
 

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_SupLicenseApplyDetailQueryRes extends JsonRes {

	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private String status;
    	private String opType;
    	private String billNo;
    	private String orgNo;
    	private String orgName;
    	private String bDate;
    	private String employeeID;
    	private String employeeName;
    	private String departID;
    	private String departName;
    	private String memo;
    	private String creatorID;
    	private String creatorName;
    	private String creatorDeptID;
    	private String creatorDeptName;
    	private String create_datetime;
    	private String lastModifyID;
    	private String lastModifyName;
    	private String lastModify_DateTime;
    	private String ownOPID;
    	private String ownOPName;
    	private String ownDeptID;
    	private String ownDeptName;
    	private String confirmBy;
    	private String confirmByName;
    	private String confirm_DateTime;
    	private String cancelBy;
    	private String cancelByName;
    	private String cancel_DateTime;

 
		private List<DetailList> detailList;
 
    }

    @Getter
	@Setter
	public class DetailList {
    	private String item;
    	private String supplierNo;
    	private String supplierName;
    	private String licenseType;
    	private String licenseNo;
    	private String beginDate;
    	private String endDate;
    	private String status;
    	private String licenseImg;

	}
  

 
}
