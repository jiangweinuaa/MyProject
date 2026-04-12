package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MachineAppQueryReq;
import com.dsc.spos.json.cust.res.DCP_MachineAppQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_MachineAppQuery extends SPosBasicService<DCP_MachineAppQueryReq,DCP_MachineAppQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_MachineAppQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MachineAppQueryReq> getRequestType()
	{
		return new TypeToken<DCP_MachineAppQueryReq>(){};
	}

	@Override
	protected DCP_MachineAppQueryRes getResponseType()
	{
		return new DCP_MachineAppQueryRes();
	}

	@Override
	protected DCP_MachineAppQueryRes processJson(DCP_MachineAppQueryReq req) throws Exception
	{
		DCP_MachineAppQueryRes res=this.getResponse();

		//单头查询
		String sql=this.getQuerySql(req);	
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_MachineAppQueryRes.level1Elm>());
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			for (Map<String, Object> oneData : getQDataDetail) 
			{
				DCP_MachineAppQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setAppName(oneData.get("APPNAME").toString());
				lv1.setAppType(oneData.get("APPNO").toString());
				
				res.getDatas().add(lv1);
				lv1=null;				
			}
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_MachineAppQueryReq req) throws Exception
	{
		String sql=null;		

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from PLATFORM_APP a "
				+ "inner join DCP_REGEDISTMODULAR b on a.rfuncno=b.rfuncno  "
				+ "where a.isregbymachine='Y' ");

		sql = sqlbuf.toString();
		return sql;
	}



}
