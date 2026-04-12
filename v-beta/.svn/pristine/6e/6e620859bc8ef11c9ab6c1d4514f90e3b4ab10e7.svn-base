package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsSetGoodpriceQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetGoodpriceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetGoodpriceQuery extends SPosBasicService<DCP_GoodsSetGoodpriceQueryReq,DCP_GoodsSetGoodpriceQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetGoodpriceQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetGoodpriceQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetGoodpriceQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetGoodpriceQueryRes getResponseType() 
	{
		return new DCP_GoodsSetGoodpriceQueryRes();
	}

	@Override
	protected DCP_GoodsSetGoodpriceQueryRes processJson(DCP_GoodsSetGoodpriceQueryReq req) throws Exception 
	{
		String sql = null;		

		//查詢資料
		DCP_GoodsSetGoodpriceQueryRes res = null;
		res = this.getResponse();
		
		//单头总数
		sql = this.getCountSql(req);				
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
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
				
				
		//单头总数
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		res.setDatas(new ArrayList<DCP_GoodsSetGoodpriceQueryRes.level1Elm>());
		
		if (getQData != null && getQData.isEmpty() == false) 
		{
			Map<String, Boolean> map_condition=new HashMap<String, Boolean>();
			map_condition.put("PLUNO", true);
			
			List<Map<String, Object>> getQDataFunc =MapDistinct.getMap(getQData, map_condition);	
			
			for (Map<String, Object> oneData : getQDataFunc) 
			{
				DCP_GoodsSetGoodpriceQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setStatus(oneData.get("GOODS_PRICE_STATUS").toString());
				oneLv1.setGoodsPrice(oneData.get("GOODS_PRICE_PRICE").toString());
				oneLv1.setPluName(oneData.get("PLU_NAME").toString());
				oneLv1.setPluNO(oneData.get("PLUNO").toString());
				oneLv1.setSupplierName(oneData.get("ORG_NAME1").toString());
				oneLv1.setSupplierNO(oneData.get("TRADE_OBJECT").toString());
				oneLv1.setUnitName(oneData.get("UNIT_NAME").toString());
				oneLv1.setUnitNO(oneData.get("UNIT").toString());

				oneLv1.setDatas(new ArrayList<DCP_GoodsSetGoodpriceQueryRes.level2Elm>());
				
				Map<String, Object> condition=new HashMap<String, Object>();
				condition.put("PLUNO", oneData.get("PLUNO").toString());					
				
				List<Map<String, Object>> getQDataDetail =MapDistinct.getWhereMap(getQData, condition, true);
				
				if(getQDataDetail!=null && getQDataDetail.isEmpty()==false)
				{
					for (Map<String, Object> oneData2 : getQDataDetail) 
					{
						//
						if(oneData2.get("ORGANIZATIONNO").toString().trim().equals(""))
							continue;
						
						DCP_GoodsSetGoodpriceQueryRes.level2Elm lv2=res.new level2Elm();
						lv2.setStatus(oneData2.get("GOODS_PRICE_ORG_STATUS").toString());
						lv2.setShopGoodPrice(oneData2.get("GOODS_PRICE_ORG_PRICE").toString());
						lv2.setShopName(oneData2.get("ORG_NAME2").toString());
						lv2.setShopId(oneData2.get("ORGANIZATIONNO").toString());
						
						oneLv1.getDatas().add(lv2);		
						lv2 = null;
					}
				}
				
				
				res.getDatas().add(oneLv1);
				
				oneLv1 = null;
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
	protected String getQuerySql(DCP_GoodsSetGoodpriceQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String keyTxt=req.getKeyTxt();


		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT A.PLUNO,C.PLU_NAME,A.UNIT,D.UNIT_NAME,A.TRADE_OBJECT,E.ORG_NAME ORG_NAME1,A.PRICE GOODS_PRICE_PRICE,A.STATUS GOODS_PRICE_STATUS,B.ORGANIZATIONNO,F.ORG_NAME ORG_NAME2,B.PRICE GOODS_PRICE_ORG_PRICE,B.STATUS GOODS_PRICE_ORG_STATUS  "
				+ "FROM DCP_GOODS_PRICE A "
				+ "LEFT JOIN DCP_GOODS_PRICE_ORG B ON A.EID=B.EID AND A.PLUNO=B.PLUNO "
				+ "LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.LANG_TYPE='"+req.getLangType()+"' "
				+ "LEFT JOIN DCP_UNIT_LANG D ON A.EID=D.EID AND A.UNIT=D.UNIT AND D.LANG_TYPE='"+req.getLangType()+"' "
				+ "LEFT JOIN DCP_ORG_LANG E ON A.EID=E.EID AND A.TRADE_OBJECT=E.ORGANIZATIONNO AND E.LANG_TYPE='"+req.getLangType()+"' "
				+ "LEFT JOIN DCP_ORG_LANG F ON A.EID=F.EID AND B.ORGANIZATIONNO=F.ORGANIZATIONNO AND F.LANG_TYPE='"+req.getLangType()+"' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.TRADE_TYPE='0' ");

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.PLUNO LIKE '%%"+ keyTxt +"%%' OR C.PLU_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}
		sqlbuf.append("AND A.PLUNO IN "
				+ "( "
				+ "SELECT PLUNO FROM "
				+ "( "
				+ "SELECT A.PLUNO,rownum rn FROM DCP_GOODS_PRICE A "
				+ "LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.LANG_TYPE='"+req.getLangType()+"' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.TRADE_TYPE='0' ");

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.PLUNO LIKE '%%"+ keyTxt +"%%' OR C.PLU_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(")  "
				+ "where rn>"+startRow+" and rn<="+(startRow+pageSize) 
				+ ") ");


		sql=sqlbuf.toString();
		return sql;
	}
	
	
	protected String getCountSql(DCP_GoodsSetGoodpriceQueryReq req) throws Exception 
	{
		
		String sql=null;

		String keyTxt=req.getKeyTxt();

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("SELECT COUNT(*) NUM FROM DCP_GOODS_PRICE A "
				+ "LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.LANG_TYPE='"+req.getLangType()+"' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.TRADE_TYPE='0' ");

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.PLUNO LIKE '%%"+ keyTxt +"%%' OR C.PLU_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}


		sql=sqlbuf.toString();
		return sql;
	}
	

	
}
