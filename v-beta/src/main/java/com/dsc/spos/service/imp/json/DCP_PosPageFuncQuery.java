package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PosPageFuncQueryReq;
import com.dsc.spos.json.cust.res.DCP_PosPageFuncQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PosPageFuncQuery extends SPosBasicService<DCP_PosPageFuncQueryReq,DCP_PosPageFuncQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PosPageFuncQueryReq req) throws Exception 
	{				
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String pageType = req.getRequest().getPageType();
		String funcGroup = req.getRequest().getFuncGroup();    
		if(Check.Null(pageType)){
			errMsg.append("页面类型不能为空值， ");
			isFail = true;

		}
		if(Check.Null(funcGroup)){
			errMsg.append("功能分区不能为空值， ");
			isFail = true;

		}

		if (isFail)	  	
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PosPageFuncQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PosPageFuncQueryReq>(){};
	}

	@Override
	protected DCP_PosPageFuncQueryRes getResponseType() 
	{
		return new DCP_PosPageFuncQueryRes();
	}

	@Override
	protected DCP_PosPageFuncQueryRes processJson(DCP_PosPageFuncQueryReq req) throws Exception 
	{

		String sql = null;		
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		//查詢資料
		DCP_PosPageFuncQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_PosPageFuncQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			for (Map<String, Object> oneData : getQData) 
			{
				DCP_PosPageFuncQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setFuncNo(oneData.get("FUNCNO").toString());
				oneLv1.setFuncName(oneData.get("FUNCNAME").toString());			
				oneLv1.setSortId(oneData.get("SORTID").toString());				
				res.getDatas().add(oneLv1);
				oneLv1=null;
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
	protected String getQuerySql(DCP_PosPageFuncQueryReq req) throws Exception 
	{

		String sql=null;

		String eId = req.geteId();
		String pageType = req.getRequest().getPageType();
		String funcGroup = req.getRequest().getFuncGroup();


		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select * from (");
		sqlbuf.append("select A.*, B.FUNCNAME,B.SORTID from DCP_POSPAGE_FUNC a "
				+ "left join DCP_POSFUNC B on a.eid =b.eid and a.FUNCNO=b.FUNCNO "		
				+ "where a.EID='"+eId+"' "
				+ "and a.PAGETYPE='"+pageType+"' "
				+ "and a.FUNCGROUP='"+funcGroup+"' "
				+ "ORDER BY B.SORTID ) ");


		sql=sqlbuf.toString();
		return sql;

	}



}
