package com.dsc.spos.json;

import com.dsc.spos.json.cust.JsonReq;
import com.google.gson.JsonObject;

public class JsonIFReq extends JsonReq {
	
	private JsonObject request;
	private JsonObject signJson;
	
	public JsonObject getRequest() {
		return request;
	}
	public void setRequest(JsonObject request) {
		this.request = request;
	}
	public JsonObject getSignJson() {
		return signJson;
	}
	public void setSignJson(JsonObject signJson) {
		this.signJson = signJson;
	}
	
	

}
