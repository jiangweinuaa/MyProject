package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_BillDateAlterReq.NameLang;
import com.dsc.spos.json.cust.req.DCP_BillDateAlterReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_LocationQueryRes.LocationList;

import lombok.Getter;
import lombok.Setter;

/**
 * 库位
 * 
 * @date 2024-10-19
 * @author 01029
 */
@Getter
@Setter
public class DCP_LocationUpdateReq extends JsonBasicReq {

	private levelRequest request;

	@Getter
	@Setter
	public class levelRequest {
		@JSONFieldRequired
		private String orgNo;
		@JSONFieldRequired
		private String wareHouse;
		@JSONFieldRequired
		private List<LocationList> locationList;
	 

 

	}
	
	@Getter
	@Setter
	public class LocationList {
		@JSONFieldRequired
    	private String location;
		@JSONFieldRequired
    	private String locationName;
    	@JSONFieldRequired
    	private String content;
    	@JSONFieldRequired
    	private String status;
		@JSONFieldRequired
		private String sortId;
		@JSONFieldRequired
		private String locationType;
		@JSONFieldRequired
		private String wareRegionNo;
	}
 
 

}
