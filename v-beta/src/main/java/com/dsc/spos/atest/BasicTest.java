package com.dsc.spos.atest;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

public abstract class BasicTest {

	private String testField;
	
	protected abstract String getLoginXML();
	
	protected String simulationLogin(DsmDAO dao) throws Exception {
		String json = this.getLoginXML();
		DispatchService ds = DispatchService.getInstance();
		String resJson = ds.callService(json, dao);
		
		////System.out.println("login Json Res:" + resJson);
		
		ParseJson pj = new ParseJson();
		DCP_LoginRetailRes lrg = pj.jsonToBean(resJson, new TypeToken<DCP_LoginRetailRes>(){});
		pj=null;
		return lrg.getToken();
	}
}
