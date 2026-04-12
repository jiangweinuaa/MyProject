package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.AndFileFilter;

import com.dsc.spos.json.cust.req.DCP_GoodsSetSupplierQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetSupplierQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetSupplierQuery extends SPosBasicService<DCP_GoodsSetSupplierQueryReq,DCP_GoodsSetSupplierQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_GoodsSetSupplierQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetSupplierQueryReq> getRequestType()
	{
		return new TypeToken<DCP_GoodsSetSupplierQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetSupplierQueryRes getResponseType() 
	{
		return new DCP_GoodsSetSupplierQueryRes();
	}

	@Override
	protected DCP_GoodsSetSupplierQueryRes processJson(DCP_GoodsSetSupplierQueryReq req) throws Exception 
	{
		
		String sql = null;		
		
		//查詢資料
		DCP_GoodsSetSupplierQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsSetSupplierQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				String stockInAllowType = oneData.get("STOCKINALLOWTYPE").toString();
				String stockOutAllowType = oneData.get("STOCKOUTALLOWTYPE").toString();
				if (Check.Null(stockInAllowType)) stockInAllowType="0";
				if (Check.Null(stockOutAllowType)) stockOutAllowType="0";
				
				DCP_GoodsSetSupplierQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setSUPPLIER(oneData.get("SUPPLIER").toString());
				oneLv1.setSUPPLIER_NAME(oneData.get("SUPPLIER_NAME").toString());
				oneLv1.setABBR(oneData.get("ABBR").toString());
				oneLv1.setMOBILE(oneData.get("MOBILE").toString());
				oneLv1.setADDRESS(oneData.get("ADDRESS").toString());
				oneLv1.seteId(oneData.get("EID").toString());		
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setStockInAllowType(stockInAllowType);
				oneLv1.setStockOutAllowType(stockOutAllowType);
				//税别
				String taxCode=oneData.get("TAXCODE")==null?"":oneData.get("TAXCODE").toString();
				String taxName=oneData.get("TAXNAME")==null?"":oneData.get("TAXNAME").toString();
				oneLv1.setTaxCode(taxCode);
				oneLv1.setTaxName(taxName);
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
	protected String getQuerySql(DCP_GoodsSetSupplierQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String status=req.getStatus();
		String keyTxt=req.getKeyTxt();
		String rangeType = req.getRangeType();//0.全部 1.门店管理 2.ERP

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ " select count(*) over() num,row_number() over (order by A.SUPPLIER) rn, "
				+ " B.SUPPLIER,B.SUPPLIER_NAME,B.ABBR,A.MOBILE,B.ADDRESS,A.EID,A.status, "
				+ " A.STOCKINALLOWTYPE,A.STOCKOUTALLOWTYPE,A.TAXCODE,C.TAXNAME "
				+ " from DCP_SUPPLIER A "
				+ " inner join DCP_SUPPLIER_LANG B on A.EID=B.EID and A.SUPPLIER=B.SUPPLIER "
				+ " left join DCP_TAXCATEGORY C on A.EID=C.EID and A.TAXCODE=C.TAXCODE "
				+ " where A.EID='"+req.geteId()+"' AND B.LANG_TYPE='"+req.getLangType()+"'  ");

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and A.status='" + status + "' ");
			sqlbuf.append(" and B.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (B.SUPPLIER LIKE '%%"+ keyTxt +"%%' OR B.SUPPLIER_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}
		
		if(rangeType!=null&&rangeType.length()>0)
		{
			if(rangeType.equals("1"))
			{
				sqlbuf.append(" and (A.RANGETYPE<>'2' OR A.RANGETYPE IS NULL)");//不等于 ERP总部
			}
			else if(rangeType.equals("2"))
			{
				sqlbuf.append(" and (A.RANGETYPE<>'1' OR A.RANGETYPE IS NULL)");//不等于 门店管理
			}
			else 
			{
			
		
			}
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}
	
	

}
