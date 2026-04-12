package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OTakeClassQueryReq;
import com.dsc.spos.json.cust.res.DCP_OTakeClassQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OTakeClassQuery extends SPosBasicService<DCP_OTakeClassQueryReq, DCP_OTakeClassQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_OTakeClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OTakeClassQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OTakeClassQueryReq>(){};
	}

	@Override
	protected DCP_OTakeClassQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OTakeClassQueryRes();
	}

	@Override
	protected DCP_OTakeClassQueryRes processJson(DCP_OTakeClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_OTakeClassQueryRes res=new DCP_OTakeClassQueryRes();
		
		String sql="select * from OC_CATEGORY where status='100' and EID='"+req.geteId()+"'"
			+ "  order by PRIORITY ";
		List<Map<String, Object>> listslq=this.doQueryData(sql, null);
		if(listslq!=null&&!listslq.isEmpty())
		{
			res.setDatas(new ArrayList<DCP_OTakeClassQueryRes.level1Elm>());
			for (Map<String, Object> map : listslq) 
			{
				DCP_OTakeClassQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setClassNO(map.get("CATEGORYNO").toString());
				lv1.setClassName(map.get("CATEGORYNAME").toString());
				res.getDatas().add(lv1);
				lv1 = null;
		  }
		}
			
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OTakeClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
