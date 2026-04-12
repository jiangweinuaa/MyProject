package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsSetSeriesQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetSeriesQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetSeriesQuery extends SPosBasicService<DCP_GoodsSetSeriesQueryReq,DCP_GoodsSetSeriesQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_GoodsSetSeriesQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetSeriesQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetSeriesQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetSeriesQueryRes getResponseType()
	{
		return new DCP_GoodsSetSeriesQueryRes();
	}

	@Override
	protected DCP_GoodsSetSeriesQueryRes processJson(DCP_GoodsSetSeriesQueryReq req) throws Exception 
	{
		
		String sql = null;		
		
		//查詢資料
		DCP_GoodsSetSeriesQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsSetSeriesQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_GoodsSetSeriesQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setSERIESNO(oneData.get("SERIESNO").toString());
				oneLv1.setSERIES_NAME(oneData.get("SERIES_NAME").toString());
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
	protected String getQuerySql(DCP_GoodsSetSeriesQueryReq req) throws Exception 
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
				+ "select count(*) over() num,row_number() over (order by A.Seriesno) rn,B.SERIESNO,B.Series_Name,A.EID,A.status from DCP_SERIES A "
				+ "inner join DCP_SERIES_lang B on A.EID=B.EID and A.Seriesno=B.Seriesno "
				+ "where A.EID='"+req.geteId()+"' AND B.LANG_TYPE='"+req.getLangType()+"'  ");

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and A.status='" + status + "' ");
			sqlbuf.append(" and B.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (B.Seriesno LIKE '%%"+ keyTxt +"%%' OR B.SERIES_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}

	
	
	
}
