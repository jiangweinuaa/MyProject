package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateCreateReq.EmpList;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateCreateReq.OrgList;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_POrderTemplateDetailQueryRes extends JsonRes {

	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private String templateNo;
    	private String templateName;
    	private String preDay;
    	private String optionalTime;
    	private String timeType;
    	private String timeValue;
    	private String receiptOrgNo;
    	private String receiptOrgName;
    	private String status;
    	private String hqPorder;
    	private String shopType;
    	private String rdate_Type;
    	private String rdate_Add;
    	private String rdate_Values;
    	private String revoke_Day;
    	private String revoke_Time;
    	private String rdate_Times;
    	private String isAddGoods;
    	private String isShowHeadStockQty;
    	private String supplierType;
    	private String allotType;
    	private String floatScale;
    	private String createBy;
    	private String createByName;
    	private String createDeptId;
    	private String createDeptName;
    	private String createDate;
    	private String createTime;
    	private String modifyBy;
    	private String modifyByName;
    	private String modifyDate;
    	private String modifyTime;




		private List<DetailList> goodsList;
		private List<OrgList> orgList;
		private List<EmpList> empList;
    }

    @Getter
	@Setter
	public class DetailList {
    	private String item;
    	private String pluNo;
    	private String pluName;
    	private String pUnit;
    	private String pUnitName;
    	private String minQty;
    	private String maxQty;
    	private String mulQty;
    	private String defQty;
    	private String status;
    	private String groupNo;
    	private String groupName;
    	private String supplier;
    	private String supplierName;

		private String supPrice;
		private String spec;
		private String category;
		private String categoryName;
		private String baseUnit;
		private String baseUnitName;


	}
    @Getter
   	@Setter
   	public class OrgList {
    	private String orgNo;
    	private String orgName;
		private String status;
		private String isMustAllot;
		private String sortId;

    }
  
    @Getter
	@Setter
	public class EmpList {
		private String employeeNo;
		private String employeeName;
		private String status;

	}
 
}
