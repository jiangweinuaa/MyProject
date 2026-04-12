package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_OrgGoodStockQueryReq.PluList;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_OrgGoodStockQueryRes extends JsonRes {

	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

		private List<OrgPluList> orgPluList;
		
    }

    @Getter
	@Setter
	public class OrgPluList {
    	private String orgNo;
 
    	private List<PluList> pluList;
	}
    
    @Getter
	@Setter
	public class PluList {
    	private String pluNo;
    	private String featureNo;
    	private String wUnit;
    	private String wQty;
    	private String baseUnit;
    	private String baseQty;
    	private String lockQty;
    	private String onLineQty;
    	private String availableQty;
    	private String pUnit;
    	private String pQty;
    	private String availablePqty;
		private List<BatchList> batchList;

	}

	@Data
	public class BatchList{

		private String batchNo;
		private String prodDate;
		private String expDate;
		private String location;
		private String locationName;
		private String pUnit;
		private String pUnitName;
		private String pQty;
		private String baseUnit;
		private String baseUnitName;
		private String unitRatio;
		private String stockQty;
		private String availableQty;
	}
  

 
}
