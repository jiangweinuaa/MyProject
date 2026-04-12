package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsSetFlavorQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetFlavorQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetFlavorQuery extends SPosBasicService<DCP_GoodsSetFlavorQueryReq,DCP_GoodsSetFlavorQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetFlavorQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetFlavorQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetFlavorQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetFlavorQueryRes getResponseType() 
	{
		return new DCP_GoodsSetFlavorQueryRes();
	}

	@Override
	protected DCP_GoodsSetFlavorQueryRes processJson(DCP_GoodsSetFlavorQueryReq req) throws Exception 
	{
		
		String sql = null;		
		
		//查詢資料
		DCP_GoodsSetFlavorQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsSetFlavorQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_GoodsSetFlavorQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setFLAVORNO(oneData.get("FLAVORNO").toString());
				oneLv1.setFLAVORNAME(oneData.get("FLAVORNAME").toString());
				oneLv1.setPRIORITY(oneData.get("PRIORITY").toString());
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
	protected String getQuerySql(DCP_GoodsSetFlavorQueryReq req) throws Exception
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
				+ "select count(*) over() num,ROWNUM rn,A.FLAVORNO,A.FLAVORNAME,A.PRIORITY,A.EID,A.status from DCP_FLAVOR A "
				+ "where A.EID='"+req.geteId()+"' ");

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and A.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.FLAVORNO LIKE '%%"+ keyTxt +"%%' OR A.FLAVORNAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(" ORDER BY A.PRIORITY ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}

	
}
