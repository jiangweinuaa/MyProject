package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 要货未到货
 */
public class DCP_POrderNonArrivalReq extends JsonBasicReq {
	
	private levelReq request;
	
	public levelReq getRequest() {
		return request;
	}
	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	@Data
	public class levelReq{
		private List<level1Elm> datas;
		private String templateNo;
	}
	
	@Data
	public class level1Elm {
		private String featureNo;
		private String pluNo;
		private String punit;
		private String punitUdLength;
	}
}
