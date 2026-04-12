package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CustomerRegeisterQueryReq;

import com.dsc.spos.json.cust.res.DCP_CustomerRegeisterQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_CustomerRegeisterQuery extends SPosBasicService<DCP_CustomerRegeisterQueryReq,DCP_CustomerRegeisterQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_CustomerRegeisterQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_CustomerRegeisterQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CustomerRegeisterQueryReq>() {};
	}

	@Override
	protected DCP_CustomerRegeisterQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CustomerRegeisterQueryRes();
	}

	@Override
	protected DCP_CustomerRegeisterQueryRes processJson(DCP_CustomerRegeisterQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		DCP_CustomerRegeisterQueryRes res=new DCP_CustomerRegeisterQueryRes();
		
		String scurdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		
		String sqlString="select A.producttype,B.RFUNC_NAME from platform_cregisterdetail A "
				+ " left join dcp_regedistmodular_lang B on A.Producttype=B.RFUNCNO  and B.lang_type='"+req.getLangType()+"' "
				+ " where A.bdate<='"+scurdate+"' and '"+scurdate+"'<=A.edate  and (A.EID='"+req.geteId()+"' or A.EID is null  )  "  
				+" group by A.producttype,B.RFUNC_NAME  " ;
		List<Map<String, Object>> sqlheadlitList=this.doQueryData(sqlString, null);
		if(sqlheadlitList!=null&&!sqlheadlitList.isEmpty())
		{
			res.setDatas(new ArrayList<DCP_CustomerRegeisterQueryRes.level1Elm>());
			for (Map<String, Object> map : sqlheadlitList) 
			{
				DCP_CustomerRegeisterQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setrFuncNo(map.get("PRODUCTTYPE").toString());
				lv1.setrFuncName(map.get("RFUNC_NAME").toString());
				res.getDatas().add(lv1);
			}
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_CustomerRegeisterQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
