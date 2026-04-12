package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PGoodsGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsGroupQuery extends SPosBasicService<DCP_PGoodsGroupQueryReq,DCP_PGoodsGroupQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PGoodsGroupQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PluNO =req.getPluNO();

		if (Check.Null(PluNO)) 
		{
			errMsg.append("套餐商品编码不可为空值, ");
			isFail = true;
		} 	


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsGroupQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsGroupQueryReq>(){};
	}

	@Override
	protected DCP_PGoodsGroupQueryRes getResponseType() 
	{
		return new DCP_PGoodsGroupQueryRes();
	}

	@Override
	protected DCP_PGoodsGroupQueryRes processJson(DCP_PGoodsGroupQueryReq req) throws Exception 
	{
		String sql = null;		

		//查詢資料
		DCP_PGoodsGroupQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_PGoodsGroupQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{

			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			

			for (Map<String, Object> oneData : getQData) 
			{

				DCP_PGoodsGroupQueryRes.level1Elm oneLv1 = res.new level1Elm();

				oneLv1.setPluNO(oneData.get("PLUNO").toString());
				oneLv1.setPluName(oneData.get("PLUNAME").toString());
				oneLv1.setPclassNO(oneData.get("PCLASSNO").toString());
				oneLv1.setPclassName(oneData.get("PCLASSNAME").toString());
				oneLv1.setInvoWay(oneData.get("INVOWAY").toString());
				oneLv1.setCondCount(oneData.get("CONDCOUNT").toString());
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
	protected String getQuerySql(DCP_PGoodsGroupQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String PluNO=req.getPluNO();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,row_number() over (order by A.PCLASSNO) rn,A.EID,A.PLUNO,C.PLU_NAME PLUNAME, A.PCLASSNO,B.PCLASSNAME,A.INVOWAY,A.CONDCOUNT,A.status ,A.PRIORITY from DCP_PGOODSCLASS A " 
				+ "LEFT join DCP_PACKAGECLASS B on A.EID=B.EID AND A.PCLASSNO=B.PCLASSNO AND B.status='100' "
				+ "LEFT JOIN DCP_GOODS_LANG C ON A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.LANG_TYPE='"+req.getLangType()+"' AND C.status='100' "
				+ "where A.EID='"+req.geteId()+"' "
				+ "AND A.status='100'  "
				+ "AND A.PLUNO='"+PluNO+"' "
		    + "  ORDER BY A.PRIORITY ");

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}



}
