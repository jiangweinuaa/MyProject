package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PGoodsShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsShopQuery extends SPosBasicService<DCP_PGoodsShopQueryReq,DCP_PGoodsShopQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PGoodsShopQueryReq req) throws Exception 
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
	protected TypeToken<DCP_PGoodsShopQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsShopQueryReq>(){};
	}

	@Override
	protected DCP_PGoodsShopQueryRes getResponseType() 
	{
		return new DCP_PGoodsShopQueryRes();
	}

	@Override
	protected DCP_PGoodsShopQueryRes processJson(DCP_PGoodsShopQueryReq req) throws Exception 
	{
		String sql = null;		

		//查詢資料
		DCP_PGoodsShopQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuery_selectedSql(req);			

		//已选门店
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_PGoodsShopQueryRes.level1Elm>());		
		if (getQData != null && getQData.isEmpty() == false) 
		{			
			for (Map<String, Object> oneData : getQData) 
			{

				DCP_PGoodsShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setShopId(oneData.get("SHOPID").toString());
				oneLv1.setShopName(oneData.get("SHOPNAME").toString());

				res.getDatas().add(oneLv1);
				oneLv1=null;
			}
		}

		getQData=null;

		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PGoodsShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	protected String getQuery_selectedSql(DCP_PGoodsShopQueryReq req) throws Exception 
	{
		String sql=null;		

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT A.ORGANIZATIONNO SHOPID,B.SHOPNAME FROM DCP_PGOODSCLASS_SHOP A  " 
				+ "INNER JOIN "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO,B.ORG_NAME SHOPNAME FROM DCP_ORG A  "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  "
				+ "WHERE A.EID='"+req.geteId()+"' "
				+ "AND A.PLUNO='"+req.getPluNO()+"' "
				+ "order by A.ORGANIZATIONNO ");

		sql=sqlbuf.toString();
		return sql;
	}
	
	
}
