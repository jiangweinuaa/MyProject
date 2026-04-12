package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_SupLicenseApplyDetailQueryRes.DetailList;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_LocationQueryRes extends JsonRes {

	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private String orgNo;
    	private String orgName;
    	private String wareHouse;
    	private String wareHouseName;
    	private List<LocationList> locationList;
    	


    }
    @Getter
	@Setter
	public class LocationList {
    	private String location;
    	private String locationName;
    	private String content;
    	private String status;
		private String sortId;
		private String locationType;
		private String wareRegionNo;
		private String wareRegionName;
    	private String creatorID;
    	private String creatorName;
    	private String createDeptID;
    	private String createDeptName;
    	private String createDateTime;
    	private String lastModifyID;
    	private String lastModifyName;
    	private String lastModifyDateTime;

	}
 

 
}
