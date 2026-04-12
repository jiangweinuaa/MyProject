package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.dsc.spos.json.cust.req.DCP_GoodsSetShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetShopQuery extends SPosBasicService<DCP_GoodsSetShopQueryReq,DCP_GoodsSetShopQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetShopQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSetShopQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetShopQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSetShopQueryRes getResponseType() 
	{
		return new DCP_GoodsSetShopQueryRes();
	}

	@Override
	protected DCP_GoodsSetShopQueryRes processJson(DCP_GoodsSetShopQueryReq req) throws Exception 
	{
		String sql = null;		

		//查詢資料
		DCP_GoodsSetShopQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsSetShopQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{

			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			

			for (Map<String, Object> oneData : getQData) 
			{

				DCP_GoodsSetShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setCOUNTERNO(oneData.get("COUNTERNO").toString());
				oneLv1.setFPSO(oneData.get("FPSO").toString());
				oneLv1.setFPSP(oneData.get("FPSP").toString());
				oneLv1.setFSAL(oneData.get("FSAL").toString());	
				oneLv1.setFSBA(oneData.get("FSBA").toString());
				oneLv1.setFSOD(oneData.get("FSOD").toString());
				oneLv1.setIS_AUTO_SUBTRACT(oneData.get("IS_AUTO_SUBTRACT").toString());
				oneLv1.setMAX_QTY(oneData.get("MAX_QTY").toString());
				oneLv1.setMIN_QTY(oneData.get("MIN_QTY").toString());
				oneLv1.setMUL_QTY(oneData.get("MUL_QTY").toString());
				oneLv1.setPLUNAME(oneData.get("PLU_NAME").toString());
				oneLv1.setPLUNO(oneData.get("PLUNO").toString());
				oneLv1.setSAFE_QTY(oneData.get("SAFE_QTY").toString());
				oneLv1.setShopId(oneData.get("ORGANIZATIONNO").toString());
				oneLv1.setSHOPNAME(oneData.get("ORG_NAME").toString());
				oneLv1.setSTYPE(oneData.get("STYPE").toString());
				oneLv1.setSUPPLIERNAME(oneData.get("SUPPLIERNAME").toString());
				oneLv1.setSUPPLIER(oneData.get("SUPPLIER").toString());
				oneLv1.setTAXCODE(oneData.get("TAXCODE").toString());
				oneLv1.setTAXNAME(oneData.get("TAXNAME").toString());
				oneLv1.setWARNING_QTY(oneData.get("WARNING_QTY").toString());
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
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_GoodsSetShopQueryReq req) throws Exception 
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String status=req.getStatus();
		String keyTxt=req.getKeyTxt();
		String Selshop=req.getSelshop();


		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "SELECT count(*) over() num,rownum rn,A.*,B.PLU_NAME,C.TAXNAME,D.ORG_NAME,(CASE WHEN A.STYPE='0' THEN F.ORG_NAME ELSE E.SUPPLIER_NAME END) SUPPLIERNAME FROM DCP_GOODS_SHOP A "
				+ "LEFT JOIN DCP_GOODS_LANG B ON A.EID=B.EID AND A.PLUNO=B.PLUNO AND B.LANG_TYPE='"+req.getLangType()+"' "
				+ "LEFT JOIN DCP_TAXCATEGORY C ON A.EID=C.EID AND A.TAXCODE=C.TAXCODE AND C.status='100' "
				+ "LEFT JOIN "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO,B.ORG_NAME FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") D ON A.EID=D.EID AND A.ORGANIZATIONNO=D.ORGANIZATIONNO "
				+ "LEFT JOIN "
				+ "("
				+ "SELECT B.SUPPLIER,B.SUPPLIER_NAME,A.EID from DCP_SUPPLIER A "
				+ "LEFT JOIN DCP_SUPPLIER_LANG B on A.EID=B.EID and A.SUPPLIER=B.SUPPLIER "
				+ "WHERE A.EID='"+req.geteId()+"' "
				+ "AND A.status='100' "
				+ "AND B.LANG_TYPE='"+req.getLangType()+"' "
				+ "AND B.status='100' "
				+ ") E ON A.EID=E.EID AND A.SUPPLIER=E.SUPPLIER "
				+ "LEFT JOIN "
				+ "("
				+ "SELECT B.ORGANIZATIONNO,B.ORG_NAME,B.EID from DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B on A.EID=B.EID and A.ORGANIZATIONNO=B.ORGANIZATIONNO "
				+ "WHERE A.EID='"+req.geteId()+"' "
				+ "AND A.status='100' "
				+ "AND B.LANG_TYPE='"+req.getLangType()+"' "
				+ "AND B.status='100' "
				+ "AND A.ORG_FORM='1' "
				+ "AND A.ISDISTBR='Y' "
				+ ") F ON A.EID=F.EID AND A.SUPPLIER=F.ORGANIZATIONNO "
				+ "WHERE A.EID='"+req.geteId()+"' ");
		
		if(Selshop!=null && Selshop.length()>0)
		{
			sqlbuf.append( "AND A.ORGANIZATIONNO='"+req.getSelshop()+"' ");
		}

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and A.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.PLUNO LIKE '%%"+ keyTxt +"%%' OR B.PLU_NAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append(") where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql=sqlbuf.toString();
		return sql;
	}

	
}
