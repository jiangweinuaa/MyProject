package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PGoodsClassQueryReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsClassQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsClassQuery extends SPosBasicService<DCP_PGoodsClassQueryReq,DCP_PGoodsClassQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PGoodsClassQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsClassQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsClassQueryReq>(){};
	}

	@Override
	protected DCP_PGoodsClassQueryRes getResponseType() 
	{
		return new DCP_PGoodsClassQueryRes();
	}

	@Override
	protected DCP_PGoodsClassQueryRes processJson(DCP_PGoodsClassQueryReq req) throws Exception 
	{
		
		String sql = null;		
		
		//查詢資料
		DCP_PGoodsClassQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_PGoodsClassQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_PGoodsClassQueryRes.level1Elm oneLv1 = res.new level1Elm();
				
				oneLv1.setPclassNo(oneData.get("PCLASSNO").toString());
				oneLv1.setPclassName(oneData.get("PCLASSNAME").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
				
				res.getDatas().add(oneLv1);
				oneLv1=null;
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
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PGoodsClassQueryReq req) throws Exception 
	{
		
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String keyTxt=req.getRequest().getKeyTxt();
		String status=req.getRequest().getStatus();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,row_number() over (order by A.PCLASSNO) rn,A.EID,A.PCLASSNO,A.PCLASSNAME,A.status from DCP_PACKAGECLASS A  where A.EID='"+req.geteId()+"'   ");

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.PCLASSNO LIKE '%%"+ keyTxt +"%%' OR A.PCLASSNAME LIKE '%%"+ keyTxt +"%%' ) ");
		}
		
		if (status != null && status.length()>0)
		{
			sqlbuf.append(" AND A.status="+status+"  ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
		
	}

	
	
	
}
