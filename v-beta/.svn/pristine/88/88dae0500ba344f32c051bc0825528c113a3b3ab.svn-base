package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsSetUnitQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetUnitQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetUnitQuery extends SPosBasicService<DCP_GoodsSetUnitQueryReq,DCP_GoodsSetUnitQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_GoodsSetUnitQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetUnitQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetUnitQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetUnitQueryRes getResponseType() 
	{
		return new DCP_GoodsSetUnitQueryRes();
	}

	@Override
	protected DCP_GoodsSetUnitQueryRes processJson(DCP_GoodsSetUnitQueryReq req) throws Exception 
	{
		String sql = null;		
		
		//查詢資料
		DCP_GoodsSetUnitQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsSetUnitQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_GoodsSetUnitQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setUNIT(oneData.get("UNIT").toString());
				oneLv1.setUNIT_NAME(oneData.get("UNIT_NAME").toString());
				oneLv1.seteId(oneData.get("EID").toString());		
				oneLv1.setStatus(oneData.get("STATUS").toString());
				
				res.getDatas().add(oneLv1);
				oneLv1 = null;
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
	protected String getQuerySql(DCP_GoodsSetUnitQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String status=req.getStatus();
		String keyTxt=req.getKeyTxt();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,row_number() over (order by A.UNIT) rn,B.UNIT,B.UNIT_NAME,A.EID,A.status from DCP_UNIT A "
				+ "inner join DCP_UNIT_lang B on A.EID=B.EID and A.unit=B.unit "
				+ "where A.EID='"+req.geteId()+"' AND B.LANG_TYPE='"+req.getLangType()+"'  ");

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and A.status='" + status + "' ");
			sqlbuf.append(" and B.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (B.UNIT LIKE '%%"+ keyTxt +"%%' OR B.UNIT_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}

	
	
	
}
