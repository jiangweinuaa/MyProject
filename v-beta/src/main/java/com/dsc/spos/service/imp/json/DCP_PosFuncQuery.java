package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PosFuncQueryReq;
import com.dsc.spos.json.cust.res.DCP_PosFuncQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_PosFuncQuery extends SPosBasicService<DCP_PosFuncQueryReq,DCP_PosFuncQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PosFuncQueryReq req) throws Exception 
	{		
		return false;		
	}

	@Override
	protected TypeToken<DCP_PosFuncQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PosFuncQueryReq>(){};
	}

	@Override
	protected DCP_PosFuncQueryRes getResponseType() 
	{
		return new DCP_PosFuncQueryRes();
	}

	@Override
	protected DCP_PosFuncQueryRes processJson(DCP_PosFuncQueryReq req) throws Exception 
	{
		
		String sql = null;		
		
		//查詢資料
		DCP_PosFuncQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_PosFuncQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			/*String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	*/		
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_PosFuncQueryRes.level1Elm oneLv1 = res.new level1Elm();
							
				oneLv1.setFuncNo(oneData.get("FUNCNO").toString());
				oneLv1.setFuncName(oneData.get("FUNCNAME").toString());
				oneLv1.setIcon(oneData.get("ICON").toString());
				oneLv1.setQss(oneData.get("QSS").toString());
				oneLv1.setSortId(oneData.get("SORTID").toString());
				oneLv1.setShortcutKey(oneData.get("SHORTCUTKEY").toString());
				
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}
			
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;			
		}
		
		/*res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);*/
		
		return res;
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PosFuncQueryReq req) throws Exception 
	{
		
		String sql=null;
		
		String eId = req.geteId();
		String keyTxt = null;
		String status = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			
			
		}
		
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}

		/*int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;*/

		

		StringBuffer sqlbuf=new StringBuffer("");
		/*sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,row_number() over (order by PAYCODE) rn,EID,PAYCODE,PAYNAME,PAYCODEERP,STATUS from DCP_PAYMENT where EID='"+req.geteId()+"' ");

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and STATUS='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (PAYCODE LIKE '%%"+ keyTxt +"%%' OR PAYNAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));*/
		
		sqlbuf.append("select * from DCP_POSFUNC where EID='"+req.geteId()+"' ");
		//只查询visable=1的数据。
		sqlbuf.append(" and VISIBLE=1 ");
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (FUNCNO LIKE '%%"+ keyTxt +"%%' OR FUNCNAME LIKE '%%"+ keyTxt +"%%' ) ");
		}
		sqlbuf.append(" order by SORTID ");
		sql=sqlbuf.toString();
		return sql;
		
	}

	
	
}
