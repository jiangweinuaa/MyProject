package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_JobSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_JobSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;

public class DCP_JobSetQuery extends SPosBasicService<DCP_JobSetQueryReq,DCP_JobSetQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_JobSetQueryReq req) throws Exception 
	{
		return false;
	}

	@Override
	protected TypeToken<DCP_JobSetQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_JobSetQueryReq>(){};
	}

	@Override
	protected DCP_JobSetQueryRes getResponseType() 
	{
		return new DCP_JobSetQueryRes();
	}

	@Override
	protected DCP_JobSetQueryRes processJson(DCP_JobSetQueryReq req) throws Exception 
	{
		String sql = null;		
		
		//查詢資料
		DCP_JobSetQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_JobSetQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{				
				DCP_JobSetQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setJobName(oneData.get("JOB_NAME").toString());
				oneLv1.setJobDescription(oneData.get("JOB_DISCRETION").toString());
				if(req.getLangType().equals("zh_TW"))
				{
					oneLv1.setJobDescription(ZHConverter.convert(oneData.get("JOB_DISCRETION").toString(),0));	
				}
				oneLv1.setJobTime(oneData.get("JOB_TIME").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setKettle(oneData.get("ETL_KETTLE").toString());
				oneLv1.setKettleFolder(oneData.get("JOB_FOLDER").toString());
				oneLv1.setMainType(oneData.get("MAINTYPE").toString());
				oneLv1.setOnSale(oneData.get("ONSALE").toString());
				oneLv1.setInitJobTime(oneData.get("INIT_JOB_TIME").toString());
				oneLv1.setInitCnfflg(oneData.get("INIT_CNFFLG").toString());
				res.getDatas().add(oneLv1);
			}
			
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;			
		}
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_JobSetQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String keyTxt=req.getRequest().getKeyTxt();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,row_number() over (order by MAINTYPE,etl_kettle,job_name) rn,job_quartz.* from job_quartz ");

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" WHERE (JOB_NAME LIKE '%%"+ keyTxt +"%%' OR JOB_DISCRETION LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}

}
