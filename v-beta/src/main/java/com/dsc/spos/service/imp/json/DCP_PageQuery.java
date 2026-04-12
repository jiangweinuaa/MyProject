package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PageQueryReq;
import com.dsc.spos.json.cust.res.DCP_PageQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_PageQuery extends SPosBasicService<DCP_PageQueryReq,DCP_PageQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_PageQueryReq req) throws Exception 
	{	
		return false;
	}

	@Override
	protected TypeToken<DCP_PageQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PageQueryReq>(){};
	}

	@Override
	protected DCP_PageQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PageQueryRes();
	}

	@Override
	protected DCP_PageQueryRes processJson(DCP_PageQueryReq req) throws Exception 
	{
		String sql=null;
		DCP_PageQueryRes res=this.getResponse();
		//单头总数
		sql = this.getCountSql(req);			

		String[] condCountValues = { }; //查詢條件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, condCountValues);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{ 			
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
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
		
		//
		sql=this.getQuerySql(req);
		String[] conditionValues1 = {}; //查詢條件
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			res.setDatas(new ArrayList<DCP_PageQueryRes.level1Elm>());
			
			for (Map<String, Object> oneData1 : getQDataDetail) 
			{
				DCP_PageQueryRes.level1Elm oneLv1= res.new level1Elm();
				
				String status=oneData1.get("STATUS").toString();
				String pageID=oneData1.get("PAGE_ID").toString();
				String pageIndex=oneData1.get("PAGEINDEX").toString();
				String pageName=oneData1.get("PAGENAME").toString();
				String pageType=oneData1.get("PAGETYPE").toString();
				String shopId=oneData1.get("SHOPID").toString();
				String shopName=oneData1.get("SHOPNAME").toString();
				
				oneLv1.setStatus(status);
				oneLv1.setPageID(pageID);
				oneLv1.setPageIndex(pageIndex);
				oneLv1.setPageName(pageName);
				oneLv1.setPageType(pageType);
				oneLv1.setShopId(shopId);
				oneLv1.setShopName(shopName);
				
				res.getDatas().add(oneLv1);
			}			
		}
		else
		{			
			res.setDatas(new ArrayList<DCP_PageQueryRes.level1Elm>());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_PageQueryReq req) throws Exception 
	{	
		String sql=null;
		String KeyTxt=req.getKeyTxt();
		String PageType=req.getPageType();
		String oShopId= req.getoShopId();
		String status= req.getStatus();

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		if(KeyTxt==null) KeyTxt="";
		if(PageType==null) PageType="";
		if(oShopId==null) oShopId="";
		if(status==null) status="";

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select rn, page_id,SHOPID,pageIndex,pageName,pageType,status,shopName from  "
				+ "( "
				+ "select a.page_id,a.SHOPID,a.pageIndex,a.pageName,a.pageType,a.status,b.SHOPNAME,rownum rn from  ta_page a inner join  "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") b " 
				+ "on a.EID=b.EID and a.SHOPID=b.SHOPID   "
				+ "and b.lang_type='"+req.getLangType()+"'   "
				+ "where a.EID='"+req.geteId()+"' "
			);
		
		if (PageType != null && PageType.length()>0)
		{
			sqlbuf.append("and a.pagetype='"+PageType+"'");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append("and ( a.pageindex like '"+KeyTxt+"%' or a.pageName like '"+KeyTxt+"%')");
		}

		if (status != null && status.length()>0)
		{
			sqlbuf.append("and a.status='"+status+"'");
		}

		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append("and a.SHOPID='"+oShopId+"'");
		}
		else
		{			
			sqlbuf.append("and  a.SHOPID in (select SHOPID  from platform_staffs_shop where EID='"+req.geteId()+"' and opno='"+req.getOpNO()+"')");
		}

		sqlbuf.append( ") where rn>"+startRow+" and rn<="+(startRow+pageSize)+"");


		sql=sqlbuf.toString();

		return sql;		
	}
	
	protected String getCountSql(DCP_PageQueryReq req) throws Exception
	{
		String sql=null;
		String KeyTxt=req.getKeyTxt();
		String PageType=req.getPageType();
		String oShopId= req.getoShopId();
		String status= req.getStatus();
		
		if(KeyTxt==null) KeyTxt="";
		if(PageType==null) PageType="";
		if(oShopId==null) oShopId="";
		if(status==null) status="";
		
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select num from "
				+ "( "
				+ "select count(*) as num from  ta_page a inner join "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") b " 
				+ "on a.EID=b.EID and a.SHOPID=b.SHOPID   "
				+ "and b.lang_type='"+req.getLangType()+"'   "
				+ "where a.EID='"+req.geteId()+"' ");
		
		if (PageType != null && PageType.length()>0)
		{
			sqlbuf.append("and a.pagetype='"+PageType+"'");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append("and ( a.pageindex like '"+KeyTxt+"%' or a.pageName like '"+KeyTxt+"%')");
		}
		
		if (status != null && status.length()>0)
		{
			sqlbuf.append("and a.status='"+status+"'");
		}

		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append("and a.SHOPID='"+oShopId+"'");
		}
		else
		{			
			sqlbuf.append("and  a.SHOPID in (select SHOPID  from platform_staffs_shop where EID='"+req.geteId()+"' and opno='"+req.getOpNO()+"')");
		}

		sqlbuf.append( ")");

		sql=sqlbuf.toString();

		return sql;		
	}
}
