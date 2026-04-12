package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SupplierGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupplierGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_SupplierGoodsQuery extends SPosBasicService<DCP_SupplierGoodsQueryReq,DCP_SupplierGoodsQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SupplierGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SupplierGoodsQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupplierGoodsQueryReq>(){};
	}

	@Override
	protected DCP_SupplierGoodsQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SupplierGoodsQueryRes();
	}

	@Override
	protected DCP_SupplierGoodsQueryRes processJson(DCP_SupplierGoodsQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		String sql=null;
		DCP_SupplierGoodsQueryRes res = null;
		res =this.getResponse();
		
		
		
		
		
		
		return res;
		
		////此服务之前用于团务处理，3.0团务暂时不搞，代码全部注释掉
		
		
		
		
		
		
		
		
		
//		//给分页字段赋值
//		sql = this.getQuerySql_Count(req);			//查询总笔数
//		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
//		int totalRecords;								//总笔数
//		int totalPages;									//总页数
//		if (getQData_Count != null && getQData_Count.isEmpty() == false)
//		{ 
//			Map<String, Object> oneData_Count = getQData_Count.get(0);
//			String num = oneData_Count.get("NUM").toString();
//			totalRecords=Integer.parseInt(num);
//			//算總頁數
//			totalPages = totalRecords / req.getPageSize();
//			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
//		}
//		else
//		{
//			totalRecords = 0;
//			totalPages = 0;
//		}
//
//		res.setPageNumber(req.getPageNumber());
//		res.setPageSize(req.getPageSize());
//		res.setTotalRecords(totalRecords);
//		res.setTotalPages(totalPages);
//
//		sql = null;
//		sql = this.getQueryByPageSql(req);	
//		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
//		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
//		{		
//			//单头主键字段
//			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
//			condition.put("SUPPLIER", true);	
//			//调用过滤函数
//			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
//			res.setDatas(new ArrayList<DCP_SupplierGoodsQueryRes.level1Elm>());
//			for(Map<String, Object> oneData : getQHeader)
//			{
//				DCP_SupplierGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
//				oneLv1.setDatas(new ArrayList<DCP_SupplierGoodsQueryRes.level2Elm>());
//				String supplierNO = oneData.get("SUPPLIER").toString();
//				String stockInAllowType  = oneData.get("STOCKINALLOWTYPE").toString();
//				String stockOutAllowType = oneData.get("STOCKOUTALLOWTYPE").toString();
//				if (Check.Null(stockInAllowType)) stockInAllowType="0";
//				if (Check.Null(stockOutAllowType)) stockOutAllowType="0";
//
//				oneLv1.setSupplierNO(supplierNO);
//				oneLv1.setSupplierName(oneData.get("SUPPLIER_NAME").toString());
//				oneLv1.setMobile(oneData.get("MOBILE").toString());
//				oneLv1.setAbbr(oneData.get("ABBR").toString());
//				oneLv1.setAddress(oneData.get("ADDRESS").toString());
//				oneLv1.setType(oneData.get("TYPE").toString());
//				oneLv1.setStockInAllowType(stockInAllowType);
//				oneLv1.setStockOutAllowType(stockOutAllowType);
//				//税别
//				String taxCode=oneData.get("TAXCODE")==null?"":oneData.get("TAXCODE").toString();
//				String taxName=oneData.get("TAXNAME")==null?"":oneData.get("TAXNAME").toString();
//				oneLv1.setTaxCode(taxCode);
//				oneLv1.setTaxName(taxName);
//				String status = oneData.get("STATUS").toString();//特殊处理下，给前端
//				if (status != null && status.isEmpty() == false && status.length() > 0
//						&& status.toUpperCase().equals("100"))
//				{
//					status = "100";
//				}
//				else
//				{
//					status ="0";
//				}			
//				oneLv1.setStatus(status);
//
//				for (Map<String, Object> oneData2 : getQDataDetail)
//				{
//					String item_supplierNO = oneData2.get("SUPPLIER").toString();			
//					DCP_SupplierGoodsQueryRes.level2Elm oneLv2 = res.new level2Elm();
//					if (supplierNO.equals(item_supplierNO))
//					{
//						String pluNO = oneData2.get("PLUNO").toString();
//						if (pluNO == null || pluNO.isEmpty() || pluNO.length() == 0)
//						{
//							continue;
//						}
//
//						oneLv2.setPluNO(oneData2.get("PLUNO").toString());
//						oneLv2.setPluName(oneData2.get("PLU_NAME").toString());
//						oneLv2.setUnit(oneData2.get("UNIT").toString());
//						oneLv2.setUnitName(oneData2.get("UNIT_NAME").toString());
//						oneLv2.setPrice(oneData2.get("PRICE").toString());					
//						//添加单身
//						oneLv1.getDatas().add(oneLv2);
//					}
//					else					
//					{
//						continue;
//					}
//
//				}
//				//添加单头
//				res.getDatas().add(oneLv1);
//			}
//
//		}
//		else
//		{
//			res.setDatas(new ArrayList<DCP_SupplierGoodsQueryRes.level1Elm>());
//		}


	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_SupplierGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private String getQuerySql_Count(DCP_SupplierGoodsQueryReq req) throws Exception
	{
		String eId = req.geteId();
		String langType = req.getLangType();
		String keyTxt = req.getKeyTxt();
		String getType = req.getGetType(); //供应商分类 0.普通 1.旅行社 2.导游
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append(" select count(*) over() num,row_number() over (order by A.SUPPLIER) rn,A.SUPPLIER,B.SUPPLIER_NAME,B.ABBR,A.MOBILE,B.ADDRESS,A.EID,A.status,A.TYPE,A.STOCKINALLOWTYPE,A.STOCKOUTALLOWTYPE from DCP_SUPPLIER A ");
		sqlbuf.append(" left join DCP_SUPPLIER_LANG B on A.EID=B.EID and A.SUPPLIER=B.SUPPLIER and B.Lang_Type='"+langType+"' ");
		sqlbuf.append(" where A.EID='"+eId+"' ");
		if (keyTxt != null && keyTxt.isEmpty() == false && keyTxt.length() > 0)
		{
			sqlbuf.append(" and (A.Supplier like '%%"+keyTxt+"%%' or B.SUPPLIER_NAME like '%%"+keyTxt+"%%')");
		}	

		//团务旅行社和导游查询  BY JZMA 20190111
		if (!Check.Null(getType)){
			if ( getType.equals("0") )
			{
				sqlbuf.append(" and ( A.type = '"+ getType +"'  or  A.type is null) ") ;
			}
			if ( getType.equals("1") || getType.equals("2") )
			{
				sqlbuf.append(" and  A.type = '"+ getType +"'  ") ;
			}
		}

		sqlbuf.append(")");
		sql = sqlbuf.toString();
		return sql;
	}

	private String getQueryByPageSql(DCP_SupplierGoodsQueryReq req) throws Exception
	{
		String eId = req.geteId();
		String langType = req.getLangType();
		String keyTxt = req.getKeyTxt();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append("select A.EID,A.SUPPLIER,A.MOBILE,A.status,B.SUPPLIER_NAME,B.ADDRESS,B.ABBR,"
				+ " C.PLUNO,C.UNIT,C.PRICE,D.PLU_NAME,E.UNIT_NAME,A.TYPE,A.STOCKINALLOWTYPE,A.STOCKOUTALLOWTYPE, "
				+ " A.TAXCODE,F.TAXNAME "
				+ " from DCP_SUPPLIER A ");
		sqlbuf.append(" inner join (");
		//分页主表
		String querySqlCount = getQuerySql_Count(req);
		sqlbuf.append(querySqlCount);
		sqlbuf.append(" where rn>" + startRow + " AND rn <= " + (startRow+pageSize)+"");
		sqlbuf.append(") B on A.EID=B.EID and A.Supplier=B.SUPPLIER");
		//DCP_GOODS_price
		sqlbuf.append(" left join DCP_GOODS_price C on A.EID=C.EID and A.SUPPLIER=C.TRADE_OBJECT and C.TRADE_TYPE='1'");
		sqlbuf.append(" left join DCP_GOODS_lang D on A.EID=D.EID and C.PLUNO=D.PLUNO  and D.LANG_TYPE='"+langType+"'");
		sqlbuf.append(" left join DCP_UNIT_lang E on A.EID=E.EID and C.UNIT=E.UNIT AND E.Lang_Type='"+langType+"'");
		sqlbuf.append(" left join DCP_TAXCATEGORY F on A.EID=F.EID and A.TAXCODE=F.TAXCODE ");
		sqlbuf.append(" where A.EID='"+eId+"'");
		sqlbuf.append(" order by Supplier,PLUNO");
		sqlbuf.append(")");
		sql = sqlbuf.toString();
		return sql;

	}

}
