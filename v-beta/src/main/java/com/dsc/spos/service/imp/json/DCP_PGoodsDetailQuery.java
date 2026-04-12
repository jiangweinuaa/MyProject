package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PGoodsDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsDetailQuery extends SPosBasicService<DCP_PGoodsDetailQueryReq,DCP_PGoodsDetailQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PGoodsDetailQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		String PluNO =req.getPluNO();
		String PclassNO =req.getPclassNO();
		
		if (Check.Null(PluNO)) 
		{
			errMsg.append("套餐商品编码不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(PclassNO)) 
		{
			errMsg.append("套餐类别编码不可为空值, ");
			isFail = true;
		} 	
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsDetailQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsDetailQueryReq>(){};
	}

	@Override
	protected DCP_PGoodsDetailQueryRes getResponseType() 
	{
		return new DCP_PGoodsDetailQueryRes();
	}

	@Override
	protected DCP_PGoodsDetailQueryRes processJson(DCP_PGoodsDetailQueryReq req) throws Exception 
	{
		String sql = null;		
		
		//查詢資料
		DCP_PGoodsDetailQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_PGoodsDetailQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;		
			
			res.setPluNO(getQData.get(0).get("PLUNO").toString());
			res.setPluName(getQData.get(0).get("PLUNAME").toString());
			res.setPclassNO(getQData.get(0).get("PCLASSNO").toString());
			res.setPclassName(getQData.get(0).get("PCLASSNAME").toString());
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_PGoodsDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();
				
				oneLv1.setDetaiPluNO(oneData.get("DPLUNO").toString());
				oneLv1.setDetaiPluName(oneData.get("DPLUNAME").toString());
				oneLv1.setDetailUnit(oneData.get("DUNIT").toString());
				oneLv1.setDetailUnitName(oneData.get("UNIT_NAME").toString());
				oneLv1.setInvoWay(oneData.get("INVOWAY").toString());
				oneLv1.setQty(oneData.get("QTY").toString());
				oneLv1.setExtraAmt(oneData.get("EXTRAAMT").toString());
				oneLv1.setIsSel(oneData.get("ISSEL").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
				try 
				{
					oneLv1.setPriority(oneData.get("PRIORITY").toString());
		
				} catch (Exception e) {
			// TODO: handle exception
		
				}
				
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
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PGoodsDetailQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,row_number() over (order by A.DPLUNO) rn,A.EID,A.DPLUNO,D.PLU_NAME DPLUNAME, A.INVOWAY,A.QTY,A.ISSEL,A.EXTRAAMT,A.status,A.PLUNO,C.PLU_NAME PLUNAME,A.DUNIT,E.UNIT_NAME, A.PCLASSNO,B.PCLASSNAME,A.PRIORITY  from DCP_PGOODSCLASS_DETAIL A "
				+ "LEFT join DCP_PACKAGECLASS B on A.EID=B.EID AND A.PCLASSNO=B.PCLASSNO AND B.status='100' "
				+ "LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.LANG_TYPE='"+req.getLangType()+"' AND C.status='100' "
				+ "LEFT JOIN DCP_GOODS_LANG D ON A.EID=D.EID AND A.DPLUNO=D.PLUNO AND D.LANG_TYPE='"+req.getLangType()+"' AND D.status='100' "
				+ "LEFT JOIN DCP_UNIT_LANG E ON A.EID=E.EID AND A.DUNIT=E.UNIT AND E.LANG_TYPE='"+req.getLangType()+"' AND E.status='100' "
				+ "where A.EID='"+req.geteId()+"' "
				+ "AND A.status='100' "
				+ "AND A.PLUNO='"+req.getPluNO()+"' "
				+ "AND A.PCLASSNO='"+req.getPclassNO()+"' ");		
		sqlbuf.append(" ORDER BY A.Priority ");
		
		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}

}
